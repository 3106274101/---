# 贸站通 TradeHub · 第一期可运行代码

规划全文：[docs/外贸独立站SaaS平台建设规划.md](./docs/外贸独立站SaaS平台建设规划.md)

本仓库已按规划书 **第一期 MVP** 生成可运行代码：

| 目录 | 说明 |
| --- | --- |
| `services/tradehub-api` | Java 17 + Spring Boot 3 模块化单体（租户/站点/商品/CMS/询盘/SEO/媒体） |
| `apps/admin` | Vue3 + Vite + Element Plus SaaS 后台 |
| `apps/storefront` | Nuxt3 SSR 加油机独立站模板 |
| `infra/docker-compose.yml` | 可选 MySQL / Redis（本机无 Docker 时默认用 H2） |

## 本机启动（Windows）

需要：JDK 17、Maven 3.9+、Node 20+。

**1. 启动后端（默认 H2 文件库，无需安装 MySQL）**

```powershell
cd services\tradehub-api
mvn -DskipTests package
java -jar target\tradehub-api-1.0.0.jar
```

若工作目录含中文路径，请用上面的 `java -jar`，不要用 `mvn spring-boot:run`。

健康检查：http://localhost:8080/api/store/health

**2. 启动 SaaS 后台**

```powershell
cd apps\admin
npm install
npm run dev
```

打开 http://localhost:5173

演示账号：

- 平台超管 `admin` / `admin123`
- 租户老板 `fueltech` / `fueltech123`
- 编辑 `editor` / `editor123`（仅新种子库）
- 销售 `sales` / `sales123`（仅新种子库）

**3. 启动加油机独立站**

```powershell
cd apps\storefront
npm install --registry=https://registry.npmmirror.com
npm run dev
```

打开 http://localhost:3000/en （中文：`/zh`）

## 演示数据

种子脚本会写入辉县正和机械（参考 [machineryzh.com](http://www.machineryzh.com/)）：Honesty / Intelligent / Elite / Aurora / Prestige 等系列、首页区块、加油机相关博客、一条巴基斯坦询盘。联系方式为 Cathy@machineryzh.com 与 +86 18567535165。

改完种子后如需重灌数据，删除 `services/tradehub-api/data/` 后重启后端。

## 切换 MySQL

安装 MySQL 8 并建库 `tradehub` 后：

```powershell
$env:TRADEHUB_PROFILE="mysql"
$env:MYSQL_HOST="127.0.0.1"
$env:MYSQL_USER="tradehub"
$env:MYSQL_PASSWORD="tradehub"
cd services\tradehub-api
mvn spring-boot:run
```

## 关键 URL

- 后台：http://localhost:5173
- 独立站首页：http://localhost:3000/en
- 产品：http://localhost:3000/en/products
- 询盘：http://localhost:3000/en/inquiry
- sitemap：http://localhost:3000/sitemap.xml
- robots：http://localhost:3000/robots.txt
- API 文档形态：`/api/admin/**`（需登录）与 `/api/store/**`（前台只读 + 询盘 POST）

## 已实现能力（对应规划第一期）

- 一个后台管理站点、页面区块、商品上下架、博客、询盘、媒体、301
- 角色权限矩阵（SUPER / OWNER / EDITOR / SALES）、成员管理、操作日志、改密
- 后台绑定域名；独立站按 `Host` / `X-Site-Host` 切站，`?site=` 仅用于预览
- 草稿 / 定时发布；询盘邮件通知（需配置 SMTP）；询盘 CSV 与分配
- 品牌装修写入 GA4 Measurement ID；对象存储可切 MinIO/S3
- 租户隔离（超管可切租户）
- Nuxt SSR、hreflang、canonical、OG、Organization/Product/FAQ JSON-LD、sitemap、robots
- 询盘蜜罐字段 + 简单频控
- 加油机工业模板与中英内容种子
- Docker 一键全栈、备份演练脚本、角色单测

## Docker 一键起全栈

需要本机 Docker Desktop。在仓库根目录：

```powershell
docker compose -f infra/docker-compose.yml up --build
```

- 后台 http://localhost:8081 （admin / admin123）
- 独立站 http://localhost:3000/en
- API http://localhost:8080
- MinIO 控制台 http://localhost:9001 （tradehub / tradehubsecret）

对象存储默认仍是本地磁盘。若要走 MinIO，给 API 设置 `TRADEHUB_STORAGE_TYPE=s3` 后重启。

询盘邮件：设置 `TRADEHUB_MAIL_ENABLED=true` 以及 `MAIL_HOST` / `MAIL_USERNAME` / `MAIL_PASSWORD` / `TRADEHUB_MAIL_TO`。

## 备份演练

```powershell
powershell -File scripts/backup.ps1
```

产出在 `backups/<时间戳>/`：有 mysqldump 则含 SQL，否则拷贝 H2 `data/` 与 `uploads/`。恢复 MySQL：`mysql -u tradehub -ptradehub tradehub < backups\...\tradehub.sql`。

