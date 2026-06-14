# 电商订单模块 - Spring Boot 3 + MyBatis Plus

## 项目简介

基于 Spring Boot 3.2 + MyBatis Plus 3.5 实现的电商订单管理模块，提供完整的订单 CRUD 接口。

## 技术栈

- **Spring Boot**: 3.2.0
- **MyBatis Plus**: 3.5.5
- **MySQL**: 8.0
- **Java**: 21
- **Lombok**: 自动生成 getter/setter
- **Jakarta Validation**: 参数校验

## 项目结构

```
order-service/
├── sql/
│   ├── order_tables.sql          # 数据库表结构
│   └── test-report.md            # 测试报告
├── src/main/java/com/example/order/
│   ├── OrderServiceApplication.java
│   ├── config/
│   │   ├── GlobalExceptionHandler.java
│   │   └── MybatisPlusConfig.java
│   ├── controller/
│   │   ├── ApiResponse.java
│   │   └── OrderController.java
│   ├── dto/
│   │   ├── CreateOrderRequest.java
│   │   ├── OrderDTO.java
│   │   ├── OrderItemDTO.java
│   │   ├── PageDTO.java
│   │   └── UpdateOrderRequest.java
│   ├── entity/
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── mapper/
│   │   ├── OrderItemMapper.java
│   │   └── OrderMapper.java
│   └── service/
│       ├── OrderService.java
│       └── impl/OrderServiceImpl.java
└── test/
    └── ApiFox-Test-Collection.json
```

## 快速开始

### 1. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE ecommerce CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 执行SQL脚本
USE ecommerce;
SOURCE sql/order_tables.sql;
```

### 2. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 3. 启动服务

```bash
# 编译项目
mvn clean package -DskipTests

# 启动服务
java -jar target/order-service-1.0.0.jar

# 或使用Maven
mvn spring-boot:run
```

服务启动后访问：`http://localhost:8081`

## API接口文档

### 基础信息

- **Base URL**: `http://localhost:8081/api`
- **Content-Type**: `application/json`

### 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /orders | 创建订单 |
| GET | /orders/{id} | 查询订单详情 |
| GET | /orders | 分页查询订单 |
| PUT | /orders/{id} | 更新订单 |
| DELETE | /orders/{id} | 删除订单 |
| GET | /orders/status/list | 获取状态列表 |

### 1. 创建订单

```bash
POST /api/orders

{
  "userId": 1,
  "receiverName": "张三",
  "receiverPhone": "13800138000",
  "receiverAddress": "北京市朝阳区建国路88号",
  "remark": "测试订单",
  "items": [
    {"productId": 1, "quantity": 2},
    {"productId": 3, "quantity": 1}
  ]
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "订单创建成功",
  "data": {
    "id": 10,
    "orderNo": "ORD202401151234560001",
    "totalAmount": 299.97,
    "status": "PENDING",
    "statusText": "待支付"
  }
}
```

### 2. 查询订单详情

```bash
GET /api/orders/1
```

### 3. 分页查询订单

```bash
GET /api/orders?userId=1&status=PENDING&current=1&size=10
```

### 4. 更新订单

```bash
PUT /api/orders/1

{
  "status": "PAID",
  "remark": "已支付"
}
```

### 5. 删除订单

```bash
DELETE /api/orders/10
```

## 订单状态说明

| 状态码 | 说明 | 状态中文 |
|--------|------|----------|
| PENDING | 待支付 | 待支付 |
| PAID | 已支付 | 已支付 |
| SHIPPED | 已发货 | 已发货 |
| COMPLETED | 已完成 | 已完成 |
| CANCELLED | 已取消 | 已取消 |
| REFUNDED | 已退款 | 已退款 |

## 测试

### 单元测试

```bash
mvn test
```

### ApiFox/Postman测试

1. 导入 `test/ApiFox-Test-Collection.json` 到 ApiFox 或 Postman
2. 配置环境变量 `baseUrl`: `http://localhost:8081`
3. 执行测试集合

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1705312000000
}
```

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误/业务异常 |
| 500 | 系统异常 |

## 错误码说明

| 错误信息 | 说明 |
|----------|------|
| 订单不存在 | 指定的订单ID不存在 |
| 用户不存在 | 指定的用户ID不存在 |
| 库存不足 | 商品库存不足 |
| 订单状态不允许 | 状态流转不合法 |

## 性能优化建议

1. **索引优化**：为 `user_id`, `status`, `order_no` 添加索引
2. **分页优化**：使用游标分页替代偏移分页
3. **缓存优化**：热点数据使用 Redis 缓存
4. **异步处理**：订单创建异步化，提升响应速度

## License

MIT License
