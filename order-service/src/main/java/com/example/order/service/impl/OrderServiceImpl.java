package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.order.dto.*;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.mapper.OrderMapper;
import com.example.order.mapper.OrderItemMapper;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO createOrder(CreateOrderRequest request) {
        log.info("创建订单，用户ID: {}", request.getUserId());
        
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(request.getUserId());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setRemark(request.getRemark());
        order.setStatus("PENDING");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setProductId(itemRequest.getProductId());
            item.setProductName("商品" + itemRequest.getProductId());
            item.setPrice(BigDecimal.valueOf(99.99));
            item.setQuantity(itemRequest.getQuantity());
            item.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            orderItems.add(item);
            totalAmount = totalAmount.add(item.getSubtotal());
        }
        
        order.setTotalAmount(totalAmount);
        orderMapper.insert(order);
        
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }
        
        log.info("订单创建成功，订单号: {}", order.getOrderNo());
        return getOrderById(order.getId());
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return convertToDTO(order);
    }

    @Override
    public OrderDTO getOrderByOrderNo(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return convertToDTO(order);
    }

    @Override
    public PageDTO<OrderDTO> getOrdersByUserId(Long userId, String status, Long current, Long size) {
        Page<Order> page = new Page<>(current, size);
        IPage<Order> orderPage = orderMapper.selectOrderPage(page, userId, status);
        
        List<OrderDTO> dtoList = orderPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageDTO<>(
                orderPage.getTotal(),
                orderPage.getCurrent(),
                orderPage.getSize(),
                dtoList
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO updateOrder(Long id, UpdateOrderRequest request) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }
        if (request.getRemark() != null) {
            order.setRemark(request.getRemark());
        }
        if (request.getReceiverName() != null) {
            order.setReceiverName(request.getReceiverName());
        }
        if (request.getReceiverPhone() != null) {
            order.setReceiverPhone(request.getReceiverPhone());
        }
        if (request.getReceiverAddress() != null) {
            order.setReceiverAddress(request.getReceiverAddress());
        }
        order.setUpdateTime(LocalDateTime.now());
        
        orderMapper.updateById(order);
        log.info("订单更新成功，订单ID: {}", id);
        return getOrderById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, id);
        orderItemMapper.delete(wrapper);
        
        boolean result = orderMapper.deleteById(id) > 0;
        log.info("订单删除成功，订单ID: {}", id);
        return result;
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setStatusText(getStatusText(order.getStatus()));
        dto.setRemark(order.getRemark());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setReceiverAddress(order.getReceiverAddress());
        dto.setCreateTime(order.getCreateTime());
        dto.setUpdateTime(order.getUpdateTime());
        
        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        List<OrderItemDTO> itemDTOs = items.stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setOrderId(item.getOrderId());
            itemDTO.setProductId(item.getProductId());
            itemDTO.setProductName(item.getProductName());
            itemDTO.setProductImage(item.getProductImage());
            itemDTO.setPrice(item.getPrice());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setSubtotal(item.getSubtotal());
            itemDTO.setCreateTime(item.getCreateTime());
            return itemDTO;
        }).collect(Collectors.toList());
        dto.setItems(itemDTOs);
        
        return dto;
    }

    private String getStatusText(String status) {
        return switch (status) {
            case "PENDING" -> "待支付";
            case "PAID" -> "已支付";
            case "SHIPPED" -> "已发货";
            case "COMPLETED" -> "已完成";
            case "CANCELLED" -> "已取消";
            case "REFUNDED" -> "已退款";
            default -> status;
        };
    }

    private String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String time = String.valueOf(System.currentTimeMillis());
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return "ORD" + date + time.substring(time.length() - 6) + random;
    }
}
