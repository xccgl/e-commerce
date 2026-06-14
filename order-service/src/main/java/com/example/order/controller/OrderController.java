package com.example.order.controller;

import com.example.order.dto.*;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("创建订单请求: {}", request);
        OrderDTO order = orderService.createOrder(request);
        return ResponseEntity.ok(ApiResponse.success(order, "订单创建成功"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long id) {
        log.info("查询订单详情，ID: {}", id);
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/no/{orderNo}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderByOrderNo(@PathVariable String orderNo) {
        log.info("查询订单详情，订单号: {}", orderNo);
        OrderDTO order = orderService.getOrderByOrderNo(orderNo);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageDTO<OrderDTO>>> getOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        log.info("分页查询订单，用户ID: {}, 状态: {}, 页码: {}, 每页: {}", userId, status, current, size);
        PageDTO<OrderDTO> page = orderService.getOrdersByUserId(userId, status, current, size);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderRequest request) {
        log.info("更新订单，ID: {}, 数据: {}", id, request);
        OrderDTO order = orderService.updateOrder(id, request);
        return ResponseEntity.ok(ApiResponse.success(order, "订单更新成功"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteOrder(@PathVariable Long id) {
        log.info("删除订单，ID: {}", id);
        boolean result = orderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success(result, "订单删除成功"));
    }

    @GetMapping("/status/list")
    public ResponseEntity<ApiResponse<Map<String, String>>> getStatusList() {
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("PENDING", "待支付");
        statusMap.put("PAID", "已支付");
        statusMap.put("SHIPPED", "已发货");
        statusMap.put("COMPLETED", "已完成");
        statusMap.put("CANCELLED", "已取消");
        statusMap.put("REFUNDED", "已退款");
        return ResponseEntity.ok(ApiResponse.success(statusMap));
    }
}
