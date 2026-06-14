package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ProductRequest;
import com.example.ecommerce.dto.Result;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 * RESTful API 规范实现
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    /**
     * 添加商品
     * POST /api/products/add
     */
    @PostMapping("/add")
    public ResponseEntity<Result<Product>> addProduct(@Valid @RequestBody ProductRequest request) {
        log.info("接收到添加商品请求: {}", request.getName());
        Product product = productService.addProduct(request);
        return ResponseEntity.ok(Result.success("商品添加成功", product));
    }

    /**
     * 删除商品
     * DELETE /api/products/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Result<Object>> deleteProduct(@PathVariable Long id) {
        log.info("接收到删除商品请求，ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok(Result.success("商品删除成功"));
    }

    /**
     * 更新商品
     * PUT /api/products/update/{id}
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Result<Product>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        log.info("接收到更新商品请求，ID: {}", id);
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(Result.success("商品更新成功", product));
    }

    /**
     * 查询商品详情
     * GET /api/products/query/{id}
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<Result<Product>> getProductById(@PathVariable Long id) {
        log.info("接收到查询商品请求，ID: {}", id);
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(Result.success(product));
    }

    /**
     * 分页查询商品列表
     * GET /api/products/query
     */
    @GetMapping("/query")
    public ResponseEntity<Result<Object>> queryProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name) {
        log.info("接收到分页查询商品请求，page: {}, size: {}, name: {}", page, size, name);
        var result = productService.queryProducts(page, size, name, null);
        return ResponseEntity.ok(Result.success(result));
    }
}
