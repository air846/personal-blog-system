-- 个人博客系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS personal_blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE personal_blog;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    nickname VARCHAR(50) NOT NULL COMMENT '昵称',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') DEFAULT 'ACTIVE' COMMENT '用户状态',
    role ENUM('USER', 'ADMIN') DEFAULT 'USER' COMMENT '用户角色',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 标签表
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    color VARCHAR(7) DEFAULT '#409EFF' COMMENT '标签颜色',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 文章表
CREATE TABLE IF NOT EXISTS articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文章ID',
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    summary TEXT COMMENT '文章摘要',
    content LONGTEXT NOT NULL COMMENT '文章内容',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    category_id BIGINT COMMENT '分类ID',
    status ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') DEFAULT 'DRAFT' COMMENT '文章状态',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    comment_count INT DEFAULT 0 COMMENT '评论次数',
    is_top TINYINT(1) DEFAULT 0 COMMENT '是否置顶',
    publish_time TIMESTAMP NULL COMMENT '发布时间',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标志',
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_author_id (author_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_publish_time (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 文章标签关联表
CREATE TABLE IF NOT EXISTS article_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    article_id BIGINT NOT NULL COMMENT '文章ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
    UNIQUE KEY uk_article_tag (article_id, tag_id),
    INDEX idx_article_id (article_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- 文章点赞表
CREATE TABLE IF NOT EXISTS article_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    article_id BIGINT NOT NULL COMMENT '文章ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_article_user (article_id, user_id),
    INDEX idx_article_id (article_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章点赞表';

-- 评论表
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    article_id BIGINT NOT NULL COMMENT '文章ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    parent_id BIGINT DEFAULT NULL COMMENT '父评论ID',
    content TEXT NOT NULL COMMENT '评论内容',
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'APPROVED' COMMENT '评论状态',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标志',
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE,
    INDEX idx_article_id (article_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 插入初始数据

-- 插入默认管理员用户
INSERT INTO users (username, nickname, email, password, role) VALUES 
('admin', '管理员', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN'),
('demo', '演示用户', 'demo@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'USER');
-- 密码为: 123456

-- 插入默认分类
INSERT INTO categories (name, description, sort_order) VALUES 
('技术分享', '分享技术相关的文章和经验', 1),
('生活随笔', '记录生活中的点点滴滴', 2),
('学习笔记', '学习过程中的笔记和总结', 3),
('项目实战', '实际项目开发经验分享', 4);

-- 插入默认标签
INSERT INTO tags (name, color) VALUES 
('Java', '#f89820'),
('Spring Boot', '#6db33f'),
('Vue.js', '#4fc08d'),
('JavaScript', '#f7df1e'),
('MySQL', '#4479a1'),
('Redis', '#dc382d'),
('前端开发', '#61dafb'),
('后端开发', '#68217a'),
('全栈开发', '#ff6b6b'),
('数据库', '#336791');

-- 插入示例文章
INSERT INTO articles (title, summary, content, author_id, category_id, status, publish_time) VALUES 
('欢迎使用个人博客系统', '这是一个基于Spring Boot和Vue.js开发的现代化博客系统', 
'# 欢迎使用个人博客系统\n\n这是一个功能完整的个人博客系统，具有以下特性：\n\n## 主要功能\n\n- 用户注册和登录\n- 文章发布和管理\n- 分类和标签系统\n- Markdown编辑器\n- 响应式设计\n\n## 技术栈\n\n### 后端\n- Spring Boot\n- Spring Security\n- MyBatis Plus\n- MySQL\n- Redis\n\n### 前端\n- Vue 3\n- Element Plus\n- Pinia\n- Vite\n\n开始你的博客之旅吧！', 
1, 1, 'PUBLISHED', NOW());

-- 插入文章标签关联
INSERT INTO article_tags (article_id, tag_id) VALUES 
(1, 2), -- Spring Boot
(1, 3), -- Vue.js
(1, 9); -- 全栈开发

-- 创建索引优化查询性能
CREATE INDEX idx_articles_status_publish_time ON articles(status, publish_time DESC);
CREATE INDEX idx_articles_author_status ON articles(author_id, status);
CREATE INDEX idx_articles_category_status ON articles(category_id, status);

-- 创建全文索引用于文章搜索
ALTER TABLE articles ADD FULLTEXT(title, summary, content);

COMMIT;