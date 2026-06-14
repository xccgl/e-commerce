
package com.example.ecommerce.config;

import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!productRepository.findAll().isEmpty()) {
            log.info("Data already initialized, skipping...");
            return;
        }

        log.info("Initializing sample data...");

        try {
            RegisterRequest adminRequest = new RegisterRequest("admin", "admin123", "admin@example.com");
            userService.register(adminRequest);
            log.info("Created admin user");
        } catch (Exception e) {
            log.warn("Admin user may already exist: {}", e.getMessage());
        }

        try {
            RegisterRequest userRequest = new RegisterRequest("user", "user123", "user@example.com");
            userService.register(userRequest);
            log.info("Created test user");
        } catch (Exception e) {
            log.warn("Test user may already exist: {}", e.getMessage());
        }

        Product p1 = new Product();
        p1.setName("iPhone 15 Pro");
        p1.setDescription("Apple iPhone 15 Pro with A17 Pro chip");
        p1.setPrice(new BigDecimal("8999.00"));
        p1.setStock(100);
        productRepository.save(p1);

        Product p2 = new Product();
        p2.setName("MacBook Pro 14");
        p2.setDescription("Apple MacBook Pro 14-inch with M3 Pro chip");
        p2.setPrice(new BigDecimal("14999.00"));
        p2.setStock(50);
        productRepository.save(p2);

        Product p3 = new Product();
        p3.setName("AirPods Pro 2");
        p3.setDescription("Apple AirPods Pro 2 with USB-C charging");
        p3.setPrice(new BigDecimal("1899.00"));
        p3.setStock(200);
        productRepository.save(p3);

        log.info("Created sample products");

        Order order1 = new Order();
        order1.setUserId(1L);
        order1.setProductId(1L);
        order1.setQuantity(1);
        order1.setTotalPrice(new BigDecimal("8999.00"));
        order1.setStatus("COMPLETED");
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setUserId(1L);
        order2.setProductId(3L);
        order2.setQuantity(2);
        order2.setTotalPrice(new BigDecimal("3798.00"));
        order2.setStatus("PENDING");
        orderRepository.save(order2);

        Order order3 = new Order();
        order3.setUserId(2L);
        order3.setProductId(2L);
        order3.setQuantity(1);
        order3.setTotalPrice(new BigDecimal("14999.00"));
        order3.setStatus("COMPLETED");
        orderRepository.save(order3);

        log.info("Created sample orders");
        log.info("Data initialization complete!");
    }
}
