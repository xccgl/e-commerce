# 商品CRUD接口实验报告

---

## [实验任务与要求]

### 任务一：统一返回结果类
1. 创建统一返回结果类（Result），包含状态码、消息、数据、时间戳
2. 创建分页结果类（PageResult），包含分页信息
3. 实现全局配置，统一API响应格式

### 任务二：RESTful商品CRUD接口
1. 基于RESTful规范开发商品增删改查接口
   - POST /api/products/add - 添加商品
   - DELETE /api/products/delete/{id} - 删除商品
   - PUT /api/products/update/{id} - 更新商品
   - GET /api/products/query/{id} - 查询商品详情
   - GET /api/products/query - 分页查询商品列表

### 任务三：全局异常处理器
1. 处理参数校验异常
2. 处理业务异常
3. 处理接口不存在异常
4. 处理运行时异常

### 任务四：接口测试用例
1. 编写完整的测试用例
2. 使用Postman/ApiFox完成全覆盖测试

---

## [实验工具]

| 工具 | 版本 | 用途 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.2 | 后端框架 |
| Spring Data JPA | 3.2 | 数据访问 |
| SQLite | 3.x | 数据库 |
| ApiFox | 1.x | 接口测试工具 |
| Lombok | 1.18 | 代码简化 |

---

## [实验分析与设计]

### 1. 统一返回结果类设计

**设计思路**：
- 统一所有API的响应格式
- 便于前端统一处理响应
- 包含状态码、消息、数据、时间戳

**Result类结构**：
```java
{
  "code": 200,           // 状态码
  "message": "成功",      // 提示消息
  "data": {...},         // 数据
  "timestamp": "..."     // 时间戳
}
```

**PageResult类结构**：
```java
{
  "records": [...],      // 数据列表
  "page": 1,             // 当前页码
  "size": 10,            // 每页大小
  "total": 100,          // 总记录数
  "pages": 10,           // 总页数
  "hasNext": true,       // 是否有下一页
  "hasPrevious": false   // 是否有上一页
}
```

### 2. RESTful商品接口设计

| HTTP方法 | 接口路径 | 功能 | Controller方法 |
|----------|----------|------|----------------|
| POST | /api/products/add | 添加商品 | addProduct |
| DELETE | /api/products/delete/{id} | 删除商品 | deleteProduct |
| PUT | /api/products/update/{id} | 更新商品 | updateProduct |
| GET | /api/products/query/{id} | 查询商品详情 | getProductById |
| GET | /api/products/query | 分页查询 | queryProducts |

### 3. 全局异常处理器设计

**异常处理策略**：
- 使用`@RestControllerAdvice`全局拦截异常
- 根据异常类型返回不同状态码
- 统一日志记录

**处理的异常类型**：
| 异常类型 | HTTP状态码 | 说明 |
|----------|------------|------|
| BusinessException | 自定义 | 业务异常 |
| MethodArgumentNotValidException | 400 | 参数校验失败 |
| MissingServletRequestParameterException | 400 | 缺少参数 |
| NoHandlerFoundException | 404 | 接口不存在 |
| IllegalArgumentException | 400 | 非法参数 |
| RuntimeException | 500 | 运行时异常 |

---

## [实验过程]

### 1. 创建统一返回结果类

**Result.java**：
```java
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    // 成功响应
    public static <T> Result<T> success(T data) { ... }
    
    // 失败响应
    public static <T> Result<T> error(Integer code, String message) { ... }
}
```

**PageResult.java**：
```java
public class PageResult<T> {
    private List<T> records;
    private Integer page;
    private Integer size;
    private Long total;
    private Integer pages;
    private Boolean hasNext;
    private Boolean hasPrevious;
}
```

### 2. 创建业务异常类

```java
public class BusinessException extends RuntimeException {
    private final Integer code;
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
```

### 3. 创建全局异常处理器

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Object>> handleBusinessException(BusinessException e) {
        return ResponseEntity.ok(Result.error(e.getCode(), e.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Object>> handleValidationException(...) {
        // 参数校验失败处理
    }
    
    // 其他异常处理...
}
```

### 4. 创建商品请求DTO

```java
public class ProductRequest {
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称长度不能超过100个字符")
    private String name;
    
    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    private BigDecimal price;
    
    @NotNull(message = "商品库存不能为空")
    @Min(value = 0, message = "商品库存不能为负数")
    private Integer stock;
}
```

### 5. 创建商品服务层

```java
public interface ProductService {
    Product addProduct(ProductRequest request);
    void deleteProduct(Long id);
    Product updateProduct(Long id, ProductRequest request);
    Product getProductById(Long id);
    PageResult<Product> queryProducts(int page, int size, String name, String category);
}
```

### 6. 接口测试用例

| 测试用例 | 接口 | 方法 | 请求体 | 预期结果 |
|----------|------|------|--------|----------|
| 添加商品成功 | /api/products/add | POST | {"name":"测试商品","price":99.99,"stock":100} | code=200, message="商品添加成功" |
| 添加商品失败-名称重复 | /api/products/add | POST | {"name":"测试商品","price":99.99,"stock":100} | code=400, message="商品已存在" |
| 添加商品失败-参数校验 | /api/products/add | POST | {"name":"","price":-1,"stock":-1} | code=400, message="参数校验失败" |
| 删除商品成功 | /api/products/delete/1 | DELETE | - | code=200, message="商品删除成功" |
| 删除商品失败-不存在 | /api/products/delete/999 | DELETE | - | code=404, message="商品不存在" |
| 更新商品成功 | /api/products/update/1 | PUT | {"name":"更新商品","price":199.99,"stock":200} | code=200, message="商品更新成功" |
| 更新商品失败-不存在 | /api/products/update/999 | PUT | {"name":"测试","price":99.99,"stock":100} | code=404, message="商品不存在" |
| 查询商品详情成功 | /api/products/query/1 | GET | - | code=200, 返回商品信息 |
| 查询商品详情失败 | /api/products/query/999 | GET | - | code=404, message="商品不存在" |
| 分页查询商品 | /api/products/query?page=1&size=10 | GET | - | code=200, 返回分页数据 |
| 接口不存在 | /api/products/invalid | GET | - | code=404, message="接口不存在" |

### 7. 测试结果

| 测试项 | 状态 | 说明 |
|--------|------|------|
| 添加商品成功 | ✅ 通过 | 正确返回商品信息 |
| 添加商品失败-名称重复 | ✅ 通过 | 正确返回400错误 |
| 添加商品失败-参数校验 | ✅ 通过 | 正确返回参数校验错误 |
| 删除商品成功 | ✅ 通过 | 正确删除商品 |
| 删除商品失败-不存在 | ✅ 通过 | 正确返回404错误 |
| 更新商品成功 | ✅ 通过 | 正确更新商品信息 |
| 更新商品失败-不存在 | ✅ 通过 | 正确返回404错误 |
| 查询商品详情成功 | ✅ 通过 | 正确返回商品详情 |
| 查询商品详情失败 | ✅ 通过 | 正确返回404错误 |
| 分页查询商品 | ✅ 通过 | 正确返回分页数据 |
| 接口不存在 | ✅ 通过 | 正确返回404错误 |

### 8. 测试截图说明

**添加商品成功**：
```
请求: POST /api/products/add
请求体: {"name":"iPhone 15 Pro","price":8999,"stock":100}
响应:
{
  "code": 200,
  "message": "商品添加成功",
  "data": {
    "id": 1,
    "name": "iPhone 15 Pro",
    "price": 8999,
    "stock": 100
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

**添加商品失败-名称重复**：
```
请求: POST /api/products/add
请求体: {"name":"iPhone 15 Pro","price":8999,"stock":100}
响应:
{
  "code": 400,
  "message": "商品已存在: iPhone 15 Pro",
  "data": null,
  "timestamp": "2024-01-01T10:00:01"
}
```

**分页查询商品**：
```
请求: GET /api/products/query?page=1&size=5
响应:
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [...],
    "page": 1,
    "size": 5,
    "total": 20,
    "pages": 4,
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": "2024-01-01T10:00:02"
}
```

---

## [实验总结]

### 实验收获

1. **统一返回格式**：学会了使用Result类统一API响应格式，便于前端处理。

2. **RESTful规范**：掌握了RESTful API的设计原则，使用正确的HTTP方法和状态码。

3. **异常处理**：学会了使用@RestControllerAdvice实现全局异常处理，统一异常响应格式。

4. **参数校验**：掌握了使用Jakarta Validation进行参数校验，确保数据完整性。

5. **分页查询**：学会了使用Spring Data JPA的分页功能，实现高效的分页查询。

### 遇到的问题及解决

1. **问题**：分页查询时页码从0开始
   **解决**：在Service层将前端传入的页码减1，因为Spring Data JPA的页码从0开始

2. **问题**：全局异常处理器无法捕获404异常
   **解决**：在application.yml中配置`spring.mvc.throw-exception-if-no-handler-found=true`和`spring.web.resources.add-mappings=false`

3. **问题**：参数校验注解不生效
   **解决**：确保在Controller方法参数上添加`@Valid`注解

4. **问题**：自定义异常无法正确返回状态码
   **解决**：使用ResponseEntity.ok()返回，在Result中包含自定义状态码

### 需要注意的问题

1. **分页参数**：前端传入的页码通常从1开始，后端需要转换为从0开始
2. **异常日志**：异常处理器中要记录详细日志，便于问题排查
3. **参数校验**：使用注解进行参数校验，避免业务代码中重复校验
4. **事务管理**：增删改操作需要添加`@Transactional`注解
5. **RESTful规范**：使用正确的HTTP方法（POST/DELETE/PUT/GET）

---

## 项目文件清单

| 文件路径 | 说明 |
|----------|------|
| `dto/Result.java` | 统一返回结果类 |
| `dto/PageResult.java` | 分页结果类 |
| `dto/ProductRequest.java` | 商品请求DTO |
| `exception/BusinessException.java` | 业务异常类 |
| `exception/GlobalExceptionHandler.java` | 全局异常处理器 |
| `service/ProductService.java` | 商品服务接口 |
| `service/impl/ProductServiceImpl.java` | 商品服务实现 |
| `controller/ProductController.java` | 商品控制器 |
| `repository/ProductRepository.java` | 商品数据访问层 |

---

## 接口文档

### 1. 添加商品

**请求**：
```
POST /api/products/add
Content-Type: application/json

{
  "name": "商品名称",
  "description": "商品描述",
  "price": 99.99,
  "stock": 100
}
```

**响应**：
```json
{
  "code": 200,
  "message": "商品添加成功",
  "data": {
    "id": 1,
    "name": "商品名称",
    "description": "商品描述",
    "price": 99.99,
    "stock": 100,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### 2. 删除商品

**请求**：
```
DELETE /api/products/delete/{id}
```

**响应**：
```json
{
  "code": 200,
  "message": "商品删除成功",
  "data": null,
  "timestamp": "2024-01-01T10:00:00"
}
```

### 3. 更新商品

**请求**：
```
PUT /api/products/update/{id}
Content-Type: application/json

{
  "name": "更新名称",
  "description": "更新描述",
  "price": 199.99,
  "stock": 200
}
```

**响应**：
```json
{
  "code": 200,
  "message": "商品更新成功",
  "data": {
    "id": 1,
    "name": "更新名称",
    "description": "更新描述",
    "price": 199.99,
    "stock": 200,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:01:00"
  },
  "timestamp": "2024-01-01T10:01:00"
}
```

### 4. 查询商品详情

**请求**：
```
GET /api/products/query/{id}
```

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "商品名称",
    "description": "商品描述",
    "price": 99.99,
    "stock": 100,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### 5. 分页查询商品

**请求**：
```
GET /api/products/query?page=1&size=10&name=关键词
```

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [...],
    "page": 1,
    "size": 10,
    "total": 100,
    "pages": 10,
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": "2024-01-01T10:00:00"
}
```
