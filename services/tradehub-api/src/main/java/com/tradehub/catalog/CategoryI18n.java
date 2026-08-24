package com.tradehub.catalog;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("th_category_i18n")
public class CategoryI18n {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long categoryId;
    private String locale;
    private String name;
    private String description;
    private String seoTitle;
    private String seoDescription;
}
