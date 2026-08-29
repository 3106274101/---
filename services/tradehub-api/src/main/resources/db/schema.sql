CREATE TABLE th_tenant (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    contact_email VARCHAR(128),
    package_code VARCHAR(32) DEFAULT 'standard',
    expired_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_tenant_code UNIQUE (code)
);

CREATE TABLE th_site (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    default_locale VARCHAR(16) NOT NULL DEFAULT 'en',
    locales VARCHAR(255) NOT NULL DEFAULT 'en,zh',
    theme VARCHAR(64) NOT NULL DEFAULT 'industrial-fuel',
    brand_json TEXT,
    seo_json TEXT,
    status VARCHAR(16) NOT NULL DEFAULT 'live',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_site_tenant_code UNIQUE (tenant_id, code)
);

CREATE TABLE th_domain (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    site_id BIGINT NOT NULL,
    host VARCHAR(255) NOT NULL,
    is_primary TINYINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_domain_host UNIQUE (host)
);

CREATE TABLE th_user (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NULL,
    username VARCHAR(64) NOT NULL,
    password_hash VARCHAR(128) NOT NULL,
    display_name VARCHAR(64),
    email VARCHAR(128),
    role_code VARCHAR(32) NOT NULL DEFAULT 'EDITOR',
    status TINYINT NOT NULL DEFAULT 1,
    is_super_admin TINYINT NOT NULL DEFAULT 0,
    last_login_at TIMESTAMP NULL,
    failed_logins INT NOT NULL DEFAULT 0,
    locked_until TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_user_username UNIQUE (username)
);

CREATE TABLE th_category (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    parent_id BIGINT DEFAULT 0,
    slug VARCHAR(128) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    status VARCHAR(16) NOT NULL DEFAULT 'live',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE th_category_i18n (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    locale VARCHAR(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    seo_title VARCHAR(255),
    seo_description VARCHAR(512),
    CONSTRAINT uk_cat_i18n UNIQUE (category_id, locale)
);

CREATE TABLE th_product (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    category_id BIGINT,
    slug VARCHAR(160) NOT NULL,
    model VARCHAR(64),
    cover_url VARCHAR(512),
    gallery_json TEXT,
    attr_json TEXT,
    trade_json TEXT,
    status VARCHAR(16) NOT NULL DEFAULT 'draft',
    sort_order INT NOT NULL DEFAULT 0,
    featured TINYINT NOT NULL DEFAULT 0,
    published_at TIMESTAMP NULL,
    scheduled_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE th_product_i18n (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    locale VARCHAR(16) NOT NULL,
    slug VARCHAR(160),
    name VARCHAR(255) NOT NULL,
    summary VARCHAR(512),
    content TEXT,
    seo_title VARCHAR(255),
    seo_description VARCHAR(512),
    CONSTRAINT uk_prod_i18n UNIQUE (product_id, locale)
);

CREATE TABLE th_product_site (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    site_id BIGINT NOT NULL,
    visible TINYINT NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_prod_site UNIQUE (product_id, site_id)
);

CREATE TABLE th_page (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    site_id BIGINT NOT NULL,
    page_type VARCHAR(32) NOT NULL,
    slug VARCHAR(160) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'draft',
    scheduled_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE th_page_i18n (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    page_id BIGINT NOT NULL,
    locale VARCHAR(16) NOT NULL,
    title VARCHAR(255),
    seo_title VARCHAR(255),
    seo_description VARCHAR(512),
    canonical VARCHAR(512),
    og_image VARCHAR(512),
    blocks_json TEXT,
    CONSTRAINT uk_page_i18n UNIQUE (page_id, locale)
);

CREATE TABLE th_article (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    site_id BIGINT NOT NULL,
    slug VARCHAR(160) NOT NULL,
    cover_url VARCHAR(512),
    status VARCHAR(16) NOT NULL DEFAULT 'draft',
    published_at TIMESTAMP NULL,
    scheduled_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE th_article_i18n (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    article_id BIGINT NOT NULL,
    locale VARCHAR(16) NOT NULL,
    slug VARCHAR(160),
    title VARCHAR(255) NOT NULL,
    summary VARCHAR(512),
    content TEXT,
    seo_title VARCHAR(255),
    seo_description VARCHAR(512),
    CONSTRAINT uk_art_i18n UNIQUE (article_id, locale)
);

CREATE TABLE th_inquiry (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    site_id BIGINT NOT NULL,
    locale VARCHAR(16),
    product_id BIGINT,
    product_name VARCHAR(255),
    name VARCHAR(128) NOT NULL,
    company VARCHAR(128),
    email VARCHAR(128) NOT NULL,
    phone VARCHAR(64),
    country VARCHAR(64),
    whatsapp VARCHAR(64),
    quantity VARCHAR(64),
    message TEXT,
    extra_json TEXT,
    honeypot VARCHAR(128),
    utm_json TEXT,
    status VARCHAR(16) NOT NULL DEFAULT 'new',
    assigned_user_id BIGINT NULL,
    notes_json TEXT,
    next_follow_at TIMESTAMP NULL,
    starred TINYINT NOT NULL DEFAULT 0,
    source VARCHAR(64),
    ip VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE th_redirect (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    site_id BIGINT NOT NULL,
    from_path VARCHAR(255) NOT NULL,
    to_path VARCHAR(255) NOT NULL,
    code INT NOT NULL DEFAULT 301,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE th_asset (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    url VARCHAR(512) NOT NULL,
    original_name VARCHAR(255),
    mime VARCHAR(64),
    size_bytes BIGINT,
    alt VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE th_audit_log (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT,
    user_id BIGINT,
    action VARCHAR(64) NOT NULL,
    target_type VARCHAR(64),
    target_id VARCHAR(64),
    detail_json TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
