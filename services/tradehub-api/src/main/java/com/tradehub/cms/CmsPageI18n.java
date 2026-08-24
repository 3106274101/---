package com.tradehub.cms;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("th_page_i18n")
public class CmsPageI18n {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long pageId;
    private String locale;
    private String title;
    private String seoTitle;
    private String seoDescription;
    private String canonical;
    private String ogImage;
    private String blocksJson;
}
