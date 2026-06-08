# 校园二手交易平台

一个基于 Spring Boot + Vue 3 的校园二手交易平台，支持商品发布、交易、AI 智能辅助等功能。

## 功能特性

### 用户端功能
- 用户注册/登录/实名认证
- 商品浏览、搜索、收藏、发布
- 购物车、订单管理、在线交易
- 消息聊天功能
- 论坛交流、跑腿服务
- AI 智能助手（商品分析、交易建议等）
- 用户数据统计面板
- 个人设置、密码修改

### 管理端功能
- 商品审核与管理
- 用户管理（封禁/解封）
- 订单管理
- 论坛管理
- 举报处理
- 公告管理
- 角色权限管理
- 文件管理

### 技术特色
- AI 智能助手集成
- 响应式设计，支持多端适配
- 完善的权限控制
- 审计日志记录

## 技术栈

### 后端技术栈
- Java 17
- Spring Boot 2.7.x
- Spring Security + JWT
- MyBatis Plus
- MySQL/PostgreSQL
- Maven
- Lombok
- Swagger/Knife4j

### 前端技术栈
- Vue 3
- Vite
- Element Plus / Vant
- Pinia
- Vue Router
- Axios

### 开发工具
- Docker
- Git

## 项目结构

```
/workspace
├── second-market-backend-yxx/    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/echoofmemories/project/
│   │   │   │   ├── annotation/   # 自定义注解
│   │   │   │   ├── aspect/       # AOP 切面
│   │   │   │   ├── common/       # 公共类
│   │   │   │   ├── config/       # 配置类
│   │   │   │   ├── controller/   # 控制器
│   │   │   │   ├── dto/          # 数据传输对象
│   │   │   │   ├── entity/       # 实体类
│   │   │   │   ├── exception/    # 异常处理
│   │   │   │   ├── mapper/       # 数据访问层
│   │   │   │   ├── security/     # 安全相关
│   │   │   │   ├── service/      # 业务逻辑层
│   │   │   │   ├── utils/        # 工具类
│   │   │   │   └── Application.java
│   │   │   └── resources/
│   │   │       ├── mapper/       # MyBatis XML
│   │   │       └── application.yml
│   │   └── test/                 # 测试代码
│   ├── database/                 # 数据库脚本
│   ├── pom.xml
│   └── Dockerfile
│
└── second_market-Vue/            # 前端项目
    ├── src/
    │   ├── api/                  # API 接口
    │   ├── assets/               # 静态资源
    │   ├── components/           # 公共组件
    │   ├── composables/          # 组合式函数
    │   ├── i18n/                 # 国际化
    │   ├── layouts/              # 布局组件
    │   ├── neo/                  # 新版 UI
    │   ├── router/               # 路由配置
    │   ├── stores/               # 状态管理
    │   ├── styles/               # 样式文件
    │   ├── utils/                # 工具函数
    │   ├── views/                # 页面组件
    │   ├── App.vue
    │   └── main.js
    ├── vite.config.js
    ├── package.json
    └── Dockerfile
```

## 快速开始

### 环境要求

- Java 17
- Node.js 16+
- MySQL 8.0+ / PostgreSQL
- Maven 3.6+
- Docker (可选)

### 1. 后端启动

#### 数据库配置

```bash
# 创建数据库
CREATE DATABASE second_market CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 执行数据库初始化脚本
# database/ 目录下的 SQL 文件
```

#### 修改配置

编辑 `second-market-backend-yxx/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/second_market
    username: your_username
    password: your_password
```

#### 启动项目

```bash
cd second-market-backend-yxx
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8001/api` 启动。

#### API 文档

启动后访问 Swagger 文档：`http://localhost:8001/api/doc.html`

### 2. 前端启动

```bash
cd second_market-Vue
npm install
npm run dev
```

前端服务将在 `http://localhost:5173` 启动。

### 3. Docker 部署 (推荐)

```bash
# 使用 Docker 快速启动
# 配置好 .env 文件后
docker build -t second-market:latest .
docker run -p 8001:8001 --env-file .env second-market:latest
```

## 功能模块

### 用户模块
- 注册、登录、退出
- 个人资料编辑
- 密码修改
- 实名认证
- 信用评分

### 商品模块
- 商品浏览、搜索、分类
- 商品详情、评论
- 商品发布、编辑、下架
- 商品收藏

### 订单模块
- 创建订单
- 订单支付
- 订单状态管理
- 订单查询

### 交易模块
- 买卖双方聊天
- 交易评价
- 交易纠纷处理

### AI 模块
- 智能对话助手
- 商品智能分析
- 价格行情预测
- 智能托管服务
- 智能搜索

### 管理后台
- 用户管理
- 商品审核
- 订单管理
- 系统公告
- 数据统计

## 数据库设计

主要数据表：

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| product | 商品表 |
| order | 订单表 |
| favorite | 收藏表 |
| review | 评价表 |
| notice | 公告表 |
| report | 举报表 |
| forum_post | 论坛帖子 |
| forum_comment | 论坛评论 |
| campus_service | 校园服务 |
| biz_chat | 聊天消息 |
| message | 系统消息 |
| audit_log | 审计日志 |
| file_info | 文件信息 |

## 开发指南

### 代码规范

- 遵循阿里巴巴 Java 开发手册
- 使用 Git 提交规范
- 代码缩进使用 4 空格

### 常用命令

```bash
# 后端构建
mvn clean package

# 后端测试
mvn test

# 前端构建
npm run build

# 前端预览
npm run preview
```

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目仅供学习交流使用。

## 致谢

- Spring Boot 社区
- Vue 社区
- Element Plus / Vant
- MyBatis Plus

## 联系方式

如有问题请提交 Issue 或联系项目维护者。

---

**祝使用愉快！**

