# 个人博客系统

一个基于 Spring Boot + Vue 3 的现代化个人博客系统。

## 功能特性

### 前端功能
- 🎨 现代化 UI 设计，基于 Element Plus
- 📱 响应式布局，支持移动端
- 🔍 文章搜索功能
- 📝 Markdown 编辑器，支持实时预览
- 🏷️ 文章分类和标签管理
- 👤 用户注册、登录、个人中心
- 📊 文章统计（阅读量、点赞数等）

### 后端功能
- 🔐 JWT 身份认证
- 🛡️ Spring Security 安全框架
- 📄 RESTful API 设计
- 🗄️ MyBatis 数据持久化
- 📝 文章 CRUD 操作
- 🏷️ 分类和标签管理
- 👥 用户管理系统

## 技术栈

### 前端
- Vue 3
- Vue Router
- Pinia (状态管理)
- Element Plus (UI 组件库)
- Axios (HTTP 客户端)
- Markdown-it (Markdown 渲染)
- Highlight.js (代码高亮)
- Vite (构建工具)

### 后端
- Spring Boot 2.7+
- Spring Security
- MyBatis
- MySQL
- JWT
- Maven

## 项目结构

```
personal-blog-system/
├── backend/                 # 后端项目
│   ├── src/main/java/
│   │   └── com/blog/
│   │       ├── controller/  # 控制器层
│   │       ├── service/     # 服务层
│   │       ├── entity/      # 实体类
│   │       ├── mapper/      # 数据访问层
│   │       ├── config/      # 配置类
│   │       └── util/        # 工具类
│   ├── src/main/resources/
│   │   ├── application.yml  # 应用配置
│   │   └── mapper/          # MyBatis 映射文件
│   └── pom.xml             # Maven 依赖
├── frontend/               # 前端项目
│   ├── src/
│   │   ├── components/     # 组件
│   │   ├── views/          # 页面
│   │   ├── api/            # API 接口
│   │   ├── stores/         # 状态管理
│   │   └── router/         # 路由配置
│   ├── package.json        # 依赖配置
│   └── vite.config.js      # Vite 配置
└── README.md
```

## 快速开始

### 环境要求
- Java 8+
- Node.js 16+
- MySQL 5.7+
- Maven 3.6+

### 后端启动

1. 创建数据库
```sql
CREATE DATABASE blog_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改配置文件
编辑 `backend/src/main/resources/application.yml`，配置数据库连接信息。

3. 启动后端服务
```bash
cd backend
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动。

### 前端启动

1. 安装依赖
```bash
cd frontend
npm install
```

2. 启动开发服务器
```bash
npm run dev
```

前端应用将在 http://localhost:3000 启动。

## API 文档

后端启动后，可以访问 Swagger API 文档：
http://localhost:8080/swagger-ui.html

## 主要页面

- **首页** (`/`) - 文章列表、热门文章、分类导航
- **文章详情** (`/article/:id`) - 文章内容、评论互动
- **搜索页面** (`/search`) - 文章搜索结果
- **用户登录** (`/login`) - 用户登录
- **用户注册** (`/register`) - 用户注册
- **个人中心** (`/profile`) - 用户信息、文章管理
- **写文章** (`/write`) - 创建新文章
- **编辑文章** (`/edit/:id`) - 编辑已有文章

## 开发说明

### 数据库设计

主要数据表：
- `users` - 用户表
- `articles` - 文章表
- `categories` - 分类表
- `tags` - 标签表
- `article_tags` - 文章标签关联表

### 认证机制

系统使用 JWT (JSON Web Token) 进行用户认证：
- 用户登录成功后获得 JWT token
- 前端在请求头中携带 token
- 后端验证 token 有效性

### 权限控制

- 游客：可以浏览文章、搜索
- 注册用户：可以创建、编辑、删除自己的文章
- 管理员：可以管理所有内容

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证。详情请见 [LICENSE](LICENSE) 文件。

## 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 Issue
- 发送邮件

---

感谢使用个人博客系统！🎉