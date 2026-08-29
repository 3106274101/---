package com.tradehub.cms;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradehub.catalog.Product;
import com.tradehub.catalog.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublishScheduler {
    private final CmsPageMapper pageMapper;
    private final ArticleMapper articleMapper;
    private final ProductMapper productMapper;

    @Scheduled(fixedDelay = 60_000)
    public void publishDue() {
        LocalDateTime now = LocalDateTime.now();
        List<CmsPage> pages = pageMapper.selectList(new LambdaQueryWrapper<CmsPage>()
                .eq(CmsPage::getStatus, "scheduled")
                .le(CmsPage::getScheduledAt, now));
        for (CmsPage page : pages) {
            page.setStatus("live");
            pageMapper.updateById(page);
        }
        List<Article> articles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, "scheduled")
                .le(Article::getScheduledAt, now));
        for (Article article : articles) {
            article.setStatus("live");
            if (article.getPublishedAt() == null) {
                article.setPublishedAt(now);
            }
            articleMapper.updateById(article);
        }
        List<Product> products = productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, "scheduled")
                .le(Product::getScheduledAt, now));
        for (Product product : products) {
            product.setStatus("live");
            if (product.getPublishedAt() == null) {
                product.setPublishedAt(now);
            }
            productMapper.updateById(product);
        }
        int total = pages.size() + articles.size() + products.size();
        if (total > 0) {
            log.info("scheduled publish: pages={} articles={} products={}", pages.size(), articles.size(), products.size());
        }
    }
}
