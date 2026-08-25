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
- 租户隔离（超管可切租户）
- Nuxt SSR、hreflang、canonical、OG、Organization/Product/FAQ JSON-LD、sitemap、robots
- 询盘蜜罐字段 + 简单频控
- 加油机工业模板与中英内容种子
