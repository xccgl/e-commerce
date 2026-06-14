
package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    List<OrderDTO> getOrdersByUserId(Long userId);
    List<OrderDTO> getOrdersByUserIdAndStatus(Long userId, String status);
    OrderDTO getOrderById(Long orderId, Long userId);
}
