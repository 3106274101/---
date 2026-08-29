package com.tradehub.inquiry;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Data
@TableName("th_inquiry")
public class Inquiry {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long siteId;
    private String locale;
    private Long productId;
    private String productName;
    private String name;
    private String company;
    private String email;
    private String phone;
    private String country;
    private String whatsapp;
    private String quantity;
    private String message;
    private String honeypot;
    private String utmJson;
    private String status;
    private Long assignedUserId;
    private String notesJson;
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextFollowAt;
    private Integer starred;
    private String source;
    private String ip;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
