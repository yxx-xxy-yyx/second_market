# Docker 一键部署（前后端一起）

## 前置条件

- 安装 Docker Desktop（Windows/macOS）或 Docker + Docker Compose（Linux）

## 启动

在项目根目录执行：

```bash
docker compose --env-file deploy/.env -f deploy/docker-compose.yml up -d --build
```

首次启动会自动初始化 MySQL，并导入：

- `database/init.sql`
- `database/seed_demo_products_per_school.sql`

## 环境变量

复制示例文件并修改 root 密码：

```bash
cp deploy/.env.example deploy/.env
```

## 访问地址

- 前端：`http://localhost/`
- 后端：`http://localhost/api/`

## 停止/清理

停止：

```bash
docker compose -f deploy/docker-compose.yml down
```

连同数据卷一起删除（会清空数据库与上传文件）：

```bash
docker compose -f deploy/docker-compose.yml down -v
```
