
# AI驱动校园二手交易平台 - 部署指南

## 项目概述

这是一个现代化的AI驱动校园二手交易平台，支持手机/平板/网页全端适配，包含AI一键发布、AI智能托管、AI鉴定质检、AI智能搜索、AI行情参考、校园AI匹配等强大功能。

## 技术栈

**后端**：
- Spring Boot 2.7.2
- Java 17
- MyBatis-Plus 3.5.2
- PostgreSQL
- JWT认证

**前端**：
- Vue 3.5.18
- Vite 7.3.3
- Pinia 3.0.3
- Element Plus 2.11.1
- Vant 4.9.24 (移动端UI)
- Tailwind CSS 3.4.19

## 部署架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Vercel        │    │   Render        │    │  Supabase       │
│  (Frontend)     │────▶│  (Backend)      │────▶│  (Database)     │
│  *.vercel.app   │    │  *.onrender.com │    │  PostgreSQL     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 环境变量配置

### 1. 数据库 (Supabase)

1. 注册 [Supabase](https://supabase.com) 账号
2. 创建新项目
3. 在项目设置 → Database 中获取数据库连接信息
4. 运行 `/second-market-backend-yxx/database/postgres/init.sql` 初始化数据库

### 2. 后端环境变量 (Render)

在 Render 中设置以下环境变量：

```
# 数据库连接
DB_URL=jdbc:postgresql://your-supabase-host:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=your-db-password

# JWT密钥
JWT_SECRET=your-jwt-secret-key-here

# AI API配置（可选）
AI_API_KEY=your-ai-api-key
AI_API_URL=https://api.openai.com/v1

# 文件上传存储（可选）
OSS_ACCESS_KEY=your-oss-access-key
OSS_SECRET_KEY=your-oss-secret-key
OSS_BUCKET=your-bucket-name
```

### 3. 前端环境变量 (Vercel)

在 Vercel 中设置以下环境变量：

```
# API基础URL
VITE_API_BASE_URL=https://your-backend-url.onrender.com
```

## 部署步骤

### 步骤1: 部署数据库 (Supabase)

1. 登录 Supabase 并创建新项目
2. 进入 SQL Editor 执行 `/second-market-backend-yxx/database/postgres/init.sql`
3. （可选）运行 `seed_demo_users_products.sql` 等脚本添加演示数据
4. 记录数据库连接信息

### 步骤2: 部署后端 (Render)

1. 将代码推送到 GitHub 仓库
2. 在 [Render](https://render.com) 创建 Web Service
3. 配置如下：
   - **Name**: second-market-backend
   - **Region**: 选择最近的区域
   - **Branch**: main
   - **Runtime**: Docker
4. 添加环境变量
5. 点击 "Create Web Service"

### 步骤3: 部署前端 (Vercel)

1. 在 [Vercel](https://vercel.com) 导入项目
2. 配置如下：
   - **Framework Preset**: Vite
   - **Root Directory**: `second_market-Vue`
3. 添加环境变量 `VITE_API_BASE_URL`
4. 点击 "Deploy"

## 本地开发

### 后端开发

```bash
cd second-market-backend-yxx
./mvnw spring-boot:run
```

### 前端开发

```bash
cd second_market-Vue
npm install
npm run dev
```

## 功能特性

### AI功能
- ✅ AI一键发布 - 拍照自动识别商品、生成文案、智能定价
- ✅ AI智能托管 - 自动回复咨询、智能议价、动态调价
- ✅ AI鉴定质检 - 上传图片秒级鉴定真伪、成色分级
- ✅ AI行情参考 - 显示7日成交价、价格趋势、同款最低价
- ✅ AI智能搜索 - 模糊/语音搜索，精准匹配需求
- ✅ 校园AI匹配 - 按学校/距离/专业优先匹配
- ✅ AI聊天助手 - 自动回复、砍价辅助、纠纷预判
- ✅ AI风控仲裁 - 校园实名认证、纠纷AI快速判责
- ✅ 校园专属AI模块 - AI教材循环、AI闲置置换、AI校园跑腿

### UI/UX
- ✅ 极简设计风格
- ✅ 浅蓝+白色+浅灰配色
- ✅ 圆角卡片+柔和阴影
- ✅ 暗黑模式支持
- ✅ 全端响应式适配

### 国际化
- ✅ 中文支持
- ✅ 韩文支持（韩国校园场景适配）
- ✅ 韩元支付（模拟）

## 监控和维护

- 后端健康检查: `https://your-backend-url/actuator/health`
- 前端部署状态: Vercel Dashboard
- 数据库管理: Supabase Dashboard

## 故障排查

### 常见问题

1. **跨域问题**
   - 检查后端 CORS 配置
   - 确认前端 API 地址正确

2. **数据库连接失败**
   - 验证数据库连接字符串
   - 检查网络连接和防火墙

3. **部署失败**
   - 查看 Render/Vercel 构建日志
   - 确认所有依赖都正确安装

## 技术支持

如有问题，请检查：
1. 各平台的部署日志
2. 环境变量是否正确配置
3. 数据库是否正常运行

---

**注意**: 请妥善保管所有密钥，不要将其提交到代码仓库中。
