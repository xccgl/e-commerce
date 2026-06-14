package com.example.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.order.dto.*;
import com.example.order.entity.Order;

public interface OrderService extends IService<Order> {
    
    OrderDTO createOrder(CreateOrderRequest request);
    
    OrderDTO getOrderById(Long id);
    
    OrderDTO getOrderByOrderNo(String orderNo);
    
    PageDTO<OrderDTO> getOrdersByUserId(Long userId, String status, Long current, Long size);
    
    OrderDTO updateOrder(Long id, UpdateOrderRequest request);
    
    boolean deleteOrder(Long id);
}
