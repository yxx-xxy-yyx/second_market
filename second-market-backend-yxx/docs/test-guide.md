# 测试指南

## 运行测试

### 运行所有测试
```bash
mvn test
```

### 运行特定测试类
```bash
mvn test -Dtest=ProductServiceTest
```

### 运行特定测试方法
```bash
mvn test -Dtest=ProductServiceTest#testGetProductById
```

## 测试覆盖范围

建议添加以下测试：

### 1. 单元测试
- Service 层业务逻辑测试
- 工具类测试
- 实体类验证测试

### 2. 集成测试
- Controller 层 API 测试
- 数据库操作测试
- 安全认证测试

### 3. 测试工具建议
- Mockito：模拟依赖
- TestContainers：真实数据库测试
- JMeter：性能测试

## 课程作业建议

由于这是课程作业，建议重点测试：
1. 用户登录注册流程
2. 商品发布和查询
3. 订单创建和状态流转
4. 权限控制验证
