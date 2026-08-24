package com.tradehub.cms;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("th_article_i18n")
public class ArticleI18n {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long articleId;
    private String locale;
    private String slug;
    private String title;
    private String summary;
    private String content;
    private String seoTitle;
    private String seoDescription;
}
