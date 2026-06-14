package com.example.order;

import com.example.order.dto.*;
import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    void testCreateOrder() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setReceiverName("测试用户");
        request.setReceiverPhone("13800138000");
        request.setReceiverAddress("北京市朝阳区测试地址");
        request.setRemark("测试订单");

        List<CreateOrderRequest.OrderItemRequest> items = new ArrayList<>();
        CreateOrderRequest.OrderItemRequest item = new CreateOrderRequest.OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);
        items.add(item);
        request.setItems(items);

        OrderDTO order = orderService.createOrder(request);
        
        assertNotNull(order);
        assertNotNull(order.getId());
        assertNotNull(order.getOrderNo());
        assertEquals("PENDING", order.getStatus());
        assertEquals(1L, order.getUserId());
        assertEquals("测试用户", order.getReceiverName());
        assertEquals(1, order.getItems().size());
        
        log.info("创建订单成功: {}", order);
    }

    @Test
    void testGetOrderById() {
        Long orderId = 1L;
        OrderDTO order = orderService.getOrderById(orderId);
        
        assertNotNull(order);
        assertEquals(orderId, order.getId());
        log.info("查询订单成功: {}", order);
    }

    @Test
    void testGetOrdersByUserId() {
        Long userId = 1L;
        PageDTO<OrderDTO> page = orderService.getOrdersByUserId(userId, null, 1L, 10L);
        
        assertNotNull(page);
        assertTrue(page.getTotal() >= 0);
        log.info("分页查询订单成功，总数: {}", page.getTotal());
    }

    @Test
    void testUpdateOrder() {
        Long orderId = 1L;
        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setStatus("PAID");
        request.setRemark("更新测试");
        
        OrderDTO order = orderService.updateOrder(orderId, request);
        
        assertNotNull(order);
        assertEquals("PAID", order.getStatus());
        assertEquals("更新测试", order.getRemark());
        log.info("更新订单成功: {}", order);
    }

    @Test
    void testDeleteAndCreateOrder() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setReceiverName("删除测试用户");
        request.setReceiverPhone("13800138001");
        request.setReceiverAddress("上海市浦东新区测试地址");
        
        List<CreateOrderRequest.OrderItemRequest> items = new ArrayList<>();
        CreateOrderRequest.OrderItemRequest item = new CreateOrderRequest.OrderItemRequest();
        item.setProductId(2L);
        item.setQuantity(1);
        items.add(item);
        request.setItems(items);

        OrderDTO created = orderService.createOrder(request);
        assertNotNull(created);
        
        boolean deleted = orderService.deleteOrder(created.getId());
        assertTrue(deleted);
        log.info("删除订单成功");
    }

    @Test
    void testGetStatusList() {
        List<Order> orders = orderService.list();
        log.info("订单总数: {}", orders.size());
    }
}
