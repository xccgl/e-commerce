
package com.example.ecommerce.controller;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getOrders(@RequestParam(required = false) String status) {
        try {
            User currentUser = getCurrentUser();
            List<OrderDTO> orders;

            if (status != null && !status.isEmpty()) {
                orders = orderService.getOrdersByUserIdAndStatus(currentUser.getId(), status);
            } else {
                orders = orderService.getOrdersByUserId(currentUser.getId());
            }

            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            log.error("Failed to fetch orders: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        try {
            User currentUser = getCurrentUser();
            OrderDTO order = orderService.getOrderById(orderId, currentUser.getId());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            log.error("Failed to fetch order: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    private User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findById(user.getId());
    }
}
