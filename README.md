# 个人博客系统

一个基于 Spring Boot + Vue 3 的现代化个人博客系统，支持文章发布、分类管理、标签系统、用户认证等功能。

## 🚀 技术栈

### 后端
- **Spring Boot 3.2.0** - 主框架
- **Spring Security** - 安全认证
- **Spring Data JPA** - 数据持久化
- **MyBatis Plus** - ORM框架
- **MySQL** - 数据库
- **Redis** - 缓存
- **JWT** - 身份验证
- **Maven** - 项目管理

### 前端
- **Vue 3** - 前端框架
- **Vue Router** - 路由管理
- **Pinia** - 状态管理
- **Element Plus** - UI组件库
- **Axios** - HTTP客户端
- **Markdown-it** - Markdown渲染
- **Highlight.js** - 代码高亮
- **Vite** - 构建工具

## 📋 功能特性

### 用户功能
- ✅ 用户注册/登录
- ✅ 个人信息管理
- ✅ 密码修改
- ✅ JWT身份验证

### 文章功能
- ✅ 文章发布/编辑/删除
- ✅ Markdown编辑器
- ✅ 文章分类管理
- ✅ 标签系统
- ✅ 文章搜索
- ✅ 文章点赞
- ✅ 草稿保存

### 系统功能
- ✅ 响应式设计
- ✅ 代码高亮
- ✅ 文章预览
- ✅ 分页查询
- ✅ 数据缓存

## 🛠️ 快速开始

### 环境要求
- Java 17+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 后端启动

1. **克隆项目**
```bash
git clone https://github.com/air846/personal-blog-system.git
cd personal-blog-system
```

2. **配置数据库**
```sql
-- 创建数据库
CREATE DATABASE personal_blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 运行初始化脚本
source database/init.sql
```

3. **修改配置**
```yaml
# backend/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/personal_blog
    username: your_username
    password: your_password
```

4. **启动后端**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端启动

1. **安装依赖**
```bash
cd frontend
npm install
```

2. **启动开发服务器**
```bash
npm run dev
```

前端应用将在 `http://localhost:3000` 启动

## 📁 项目结构

```
personal-blog-system/
├── backend/                 # 后端项目
│   ├── src/main/java/
│   │   └── com/air846/blog/
│   │       ├── controller/  # 控制器层
│   │       ├── service/     # 服务层
│   │       ├── entity/      # 实体类
│   │       ├── dto/         # 数据传输对象
│   │       ├── config/      # 配置类
│   │       └── util/        # 工具类
│   ├── src/main/resources/
│   │   ├── application.yml  # 主配置文件
│   │   ├── application-dev.yml
│   │   └── application-prod.yml
│   └── pom.xml             # Maven配置
├── frontend/               # 前端项目
│   ├── src/
│   │   ├── api/            # API接口
│   │   ├── components/     # 公共组件
│   │   ├── views/          # 页面组件
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # 状态管理
│   │   └── main.js         # 入口文件
│   ├── package.json        # 依赖配置
│   └── vite.config.js      # Vite配置
├── database/               # 数据库脚本
│   └── init.sql           # 初始化脚本
└── README.md              # 项目说明
```

## 🔧 开发指南

### API接口

后端API基础路径：`http://localhost:8080/api`

主要接口：
- `POST /auth/login` - 用户登录
- `POST /auth/register` - 用户注册
- `GET /articles` - 获取文章列表
- `POST /articles` - 创建文章
- `GET /articles/{id}` - 获取文章详情
- `PUT /articles/{id}` - 更新文章
- `DELETE /articles/{id}` - 删除文章

### 环境变量

生产环境可通过环境变量配置：
- `DB_USERNAME` - 数据库用户名
- `DB_PASSWORD` - 数据库密码
- `REDIS_HOST` - Redis主机
- `REDIS_PORT` - Redis端口
- `REDIS_PASSWORD` - Redis密码
- `JWT_SECRET` - JWT密钥

## 🚀 部署

### 后端部署

1. **打包应用**
```bash
cd backend
mvn clean package -Pprod
```

2. **运行JAR包**
```bash
java -jar target/personal-blog-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 前端部署

1. **构建生产版本**
```bash
cd frontend
npm run build
```

2. **部署到Web服务器**
将 `dist` 目录下的文件部署到 Nginx 或其他Web服务器

### Docker部署（可选）

```bash
# 构建镜像
docker build -t personal-blog-backend ./backend
docker build -t personal-blog-frontend ./frontend

# 运行容器
docker run -d -p 8080:8080 personal-blog-backend
docker run -d -p 3000:80 personal-blog-frontend
```

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 👨‍💻 作者

**air846** - [GitHub](https://github.com/air846)

## 🙏 致谢

感谢以下开源项目：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [MyBatis Plus](https://baomidou.com/)

---

如果这个项目对你有帮助，请给个 ⭐️ 支持一下！