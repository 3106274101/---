package com.tradehub.notify;

import com.tradehub.inquiry.Inquiry;
import com.tradehub.tenant.Site;
import com.tradehub.tenant.Tenant;
import com.tradehub.tenant.TenantMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@Service
public class MailNotifyService {
    private final ObjectProvider<JavaMailSender> mailSender;
    private final TenantMapper tenantMapper;
    private final boolean enabled;
    private final String from;
    private final String defaultTo;

    public MailNotifyService(ObjectProvider<JavaMailSender> mailSender,
                             TenantMapper tenantMapper,
                             @Value("${tradehub.mail.enabled:false}") boolean enabled,
                             @Value("${tradehub.mail.from:noreply@localhost}") String from,
                             @Value("${tradehub.mail.notify-to:}") String defaultTo) {
        this.mailSender = mailSender;
        this.tenantMapper = tenantMapper;
        this.enabled = enabled;
        this.from = from;
        this.defaultTo = defaultTo;
    }

    @Async
    public void notifyInquiry(Inquiry inquiry, Site site) {
        if (!enabled) {
            return;
        }
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            log.info("mail sender not configured, skip inquiry {}", inquiry.getId());
            return;
        }
        String to = defaultTo;
        if (!StringUtils.hasText(to) && site != null) {
            Tenant tenant = tenantMapper.selectById(site.getTenantId());
            if (tenant != null) {
                to = tenant.getContactEmail();
            }
        }
        if (!StringUtils.hasText(to)) {
            log.info("no inquiry mail recipient for site {}", site == null ? null : site.getCode());
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to.split("\\s*,\\s*"));
            String siteName = site == null ? "TradeHub" : site.getName();
            message.setSubject("[TradeHub] New inquiry · " + siteName);
            message.setText("""
                    Site: %s
                    Name: %s
                    Company: %s
                    Email: %s
                    Phone: %s
                    Country: %s
                    WhatsApp: %s
                    Product: %s
                    Quantity: %s

                    Message:
                    %s
                    """.formatted(
                    siteName,
                    nullToDash(inquiry.getName()),
                    nullToDash(inquiry.getCompany()),
                    nullToDash(inquiry.getEmail()),
                    nullToDash(inquiry.getPhone()),
                    nullToDash(inquiry.getCountry()),
                    nullToDash(inquiry.getWhatsapp()),
                    nullToDash(inquiry.getProductName()),
                    nullToDash(inquiry.getQuantity()),
                    nullToDash(inquiry.getMessage())
            ));
            sender.send(message);
        } catch (Exception e) {
            log.warn("inquiry mail failed: {}", e.getMessage());
        }
    }

    public Map<String, Object> sendTest(String to) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (!enabled) {
            return Map.of("ok", false, "message", "TRADEHUB_MAIL_ENABLED is false");
        }
        if (sender == null) {
            return Map.of("ok", false, "message", "mail sender not configured");
        }
        String dest = StringUtils.hasText(to) ? to : defaultTo;
        if (!StringUtils.hasText(dest)) {
            return Map.of("ok", false, "message", "no recipient");
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(dest.split("\\s*,\\s*"));
            message.setSubject("[TradeHub] Test mail");
            message.setText("If you received this, SMTP is working.");
            sender.send(message);
            return Map.of("ok", true, "message", "sent to " + dest);
        } catch (Exception e) {
            return Map.of("ok", false, "message", e.getMessage() == null ? "send failed" : e.getMessage());
        }
    }

    private String nullToDash(String value) {
        return StringUtils.hasText(value) ? value : "-";
    }
}
