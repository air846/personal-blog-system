# 个人博客系统

一个基于 Spring Boot + Vue.js 的完整个人博客系统，适合 Spring Boot 初学者学习。

## 功能特性

### 核心功能
- ✅ 文章管理（发布、编辑、删除）
- ✅ Markdown 支持
- ✅ 分类和标签管理
- ✅ 评论系统
- ✅ 用户权限控制（管理员/普通用户）
- ✅ 文章阅读量统计
- ✅ 文章搜索功能

### 技术特性
- 🔥 热门文章缓存（Redis）
- 📊 评论缓存优化
- 🔍 全文搜索
- 📱 响应式设计

## 技术栈

### 后端
- **Spring Boot 3.2.x** - 主框架
- **MyBatis-Plus** - ORM框架
- **MySQL 8.0** - 数据库
- **Redis** - 缓存
- **Spring Security** - 安全认证
- **JWT** - 令牌认证
- **Swagger** - API文档

### 前端
- **Vue 3** - 前端框架
- **Element Plus** - UI组件库
- **Axios** - HTTP客户端
- **Vue Router** - 路由管理
- **Pinia** - 状态管理

## 项目结构

```
blog-system/
├── backend/                 # Spring Boot 后端
│   ├── src/main/java/
│   │   └── com/blog/
│   │       ├── controller/  # 控制器层
│   │       ├── service/     # 服务层
│   │       ├── mapper/      # 数据访问层
│   │       ├── entity/      # 实体类
│   │       ├── dto/         # 数据传输对象
│   │       ├── config/      # 配置类
│   │       └── utils/       # 工具类
│   └── src/main/resources/
│       ├── mapper/          # MyBatis XML
│       └── application.yml  # 配置文件
├── frontend/                # Vue.js 前端
│   ├── src/
│   │   ├── components/      # 组件
│   │   ├── views/          # 页面
│   │   ├── router/         # 路由
│   │   ├── store/          # 状态管理
│   │   └── api/            # API接口
│   └── package.json
├── database/               # 数据库脚本
└── docs/                   # 项目文档
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+

### 后端启动
```bash
cd backend
mvn spring-boot:run
```

### 快速启动

#### 方式一：使用启动脚本（推荐）
```bash
# Linux/Mac
chmod +x start.sh
./start.sh

# Windows
start.bat
```

#### 方式二：手动启动
```bash
# 启动后端
cd backend
mvn spring-boot:run

# 新开终端，启动前端
cd frontend
npm install
npm run dev
```

访问 http://localhost:3000

### 数据库初始化
```bash
# 执行 database/init.sql 脚本
mysql -u root -p < database/init.sql
```

## API 文档

启动后端服务后，访问：http://localhost:8080/swagger-ui.html

## 学习要点

这个项目涵盖了以下核心技术概念：

### 后端 (Spring Boot)
1. **分层架构**：Controller → Service → Mapper
2. **依赖注入**：Spring IoC 容器
3. **事务管理**：@Transactional 注解
4. **安全认证**：Spring Security + JWT
5. **缓存机制**：Redis 集成
6. **API 设计**：RESTful 接口
7. **数据库操作**：MyBatis-Plus 使用
8. **前后端分离**：跨域配置

### 前端 (Vue 3)
1. **组件化开发**：Vue 3 Composition API
2. **状态管理**：Pinia 状态管理
3. **路由管理**：Vue Router 4
4. **UI 组件库**：Element Plus 使用
5. **HTTP 请求**：Axios 封装
6. **构建工具**：Vite 配置
7. **Markdown 支持**：markdown-it 集成
8. **响应式设计**：移动端适配

## 开发计划

- [x] 项目架构设计
- [x] 数据库设计
- [x] 后端 API 开发
- [x] 前端页面开发
- [ ] 功能测试
- [ ] 部署文档