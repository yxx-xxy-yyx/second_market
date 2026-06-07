# 本地开发指南

## 问题诊断

### 本地无法运行的常见问题

#### 1. Java版本不匹配
- **问题**: 项目要求 Java 17，但系统使用 Java 25
- **解决**: 安装并使用 Java 17

#### 2. 缺少环境变量配置文件
- **问题**: 项目缺少 .env 和 .env.local 配置文件
- **解决**: 已自动创建，根据需要修改配置

#### 3. 数据库连接问题
- **问题**: 需要 PostgreSQL 数据库
- **解决**: 使用 H2 内存数据库（已配置）

## 快速开始

### 后端开发环境配置

#### 1. 安装 Java 17

**使用 SDKMAN (推荐)**:
```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 17.0.12-tem
sdk use java 17.0.12-tem
```

**使用 Homebrew (Mac):
```bash
brew install openjdk@17
```

**使用 apt (Ubuntu/Debian):
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

#### 2. 后端配置

确保你已经有 .env 配置文件，已自动创建。你可以根据需要修改：
- 数据库连接信息
- JWT密钥
- 其他配置

#### 3. 运行后端

```bash
cd second-market-backend-yxx
./mvnw spring-boot:run
```

后端默认启动在 http://localhost:8001/api

### 前端开发环境

#### 1. 安装依赖

```bash
cd second_market-Vue
npm install
# 或者使用 yarn (推荐
yarn install
```

#### 2. 运行前端

```bash
npm run dev
# 或者 yarn
yarn dev
```

前端默认启动在 http://localhost:5173

## 本地数据库选择

### 选项 1: H2 内存数据库 (推荐用于快速开发)

已默认配置在 .env 文件中。
- 启动会自动在内存中创建数据库，无需额外安装。

### 选项 2: PostgreSQL 数据库

如果你想使用 PostgreSQL:

1. 安装 PostgreSQL
2. 创建数据库:
```bash
createdb second_market
```
3. 修改 .env 文件:
```
DB_USERNAME=postgres
DB_PASSWORD=your-password
DB_URL=jdbc:postgresql://localhost:5432/second_market
DB_DRIVER=org.postgresql.Driver
```
4. 运行初始化脚本:
```bash
# 连接数据库并执行
psql -d second_market -f database/postgres/init.sql
```

## 检查清单

确保本地运行之前检查以下项目:

- [ ] Java 版本为 17 (不是 25)
- [ ] Node.js 版本满足要求 (>=20.19.0)
- [ ] 已正确配置了 .env 和 .env.local 文件
- [ ] Maven 和 npm/yarn 依赖已安装
- [ ] 网络连接正常

## 故障排除

### 如果遇到端口被占用

**Windows:
```bash
netstat -ano | findstr :8001
netstat -ano | findstr :5173
```

**Linux/Mac**:
```bash
lsof -i :8001
lsof -i :5173
```

### 后端构建失败
尝试清理缓存重新构建:
```bash
cd second-market-backend-yxx
./mvnw clean
./mvnw spring-boot:run
```

### 前端依赖问题
```bash
cd second_market-Vue
rm -rf node_modules package-lock.json yarn.lock
npm install
```
