
package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        log.info("Fetching orders for user: {}", userId);
        List<Order> orders = orderRepository.findByUserId(userId);
        return convertToDTOList(orders);
    }

    @Override
    public List<OrderDTO> getOrdersByUserIdAndStatus(Long userId, String status) {
        log.info("Fetching orders for user: {} with status: {}", userId, status);
        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, status);
        return convertToDTOList(orders);
    }

    @Override
    public OrderDTO getOrderById(Long orderId, Long userId) {
        log.info("Fetching order: {} for user: {}", orderId, userId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问此订单");
        }

        return convertToDTO(order);
    }

    private List<OrderDTO> convertToDTOList(List<Order> orders) {
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Order order) {
        Product product = productRepository.findById(order.getProductId()).orElse(null);
        String productName = product != null ? product.getName() : "未知商品";

        return new OrderDTO(
                order.getId(),
                order.getUserId(),
                order.getProductId(),
                productName,
                order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
