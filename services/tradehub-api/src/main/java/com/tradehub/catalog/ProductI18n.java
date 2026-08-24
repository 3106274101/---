package com.tradehub.catalog;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("th_product_i18n")
public class ProductI18n {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long productId;
    private String locale;
    private String slug;
    private String name;
    private String summary;
    private String content;
    private String seoTitle;
    private String seoDescription;
}
