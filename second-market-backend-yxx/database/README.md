# 数据库初始化说明

## 数据库类型
本项目使用 **PostgreSQL** 数据库。

## 初始化步骤

1. 创建数据库（如果还没有）：
```sql
CREATE DATABASE second_market WITH ENCODING 'UTF8';
```

2. 使用 `database/postgres/` 目录下的脚本进行初始化：
```bash
psql -d second_market -f database/postgres/init.sql
```

## 目录结构

- `database/postgres/` - PostgreSQL 专用脚本（推荐使用）
- `database/` - 旧版 MySQL 脚本（已废弃，保留用于参考）

## 脚本说明

### 主要脚本
- `init.sql` - 完整的数据库初始化脚本，包含所有表结构和初始数据
- `seed_demo_users_products.sql` - 示例数据（用户和商品）
- `seed_more_korean_schools.sql` - 更多韩国学校数据

### 补丁脚本
- `forum_module.sql` - 论坛模块
- `campus_service.sql` - 校园服务模块
- `browse_history.sql` - 浏览历史记录
- `fix_*.sql` - 各种修复补丁
