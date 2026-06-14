package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContaining(String name);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    
    /**
     * 检查商品名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 检查商品名称是否存在（排除指定ID）
     */
    boolean existsByNameAndIdNot(String name, Long id);
    
    /**
     * 分页查询商品（按名称模糊查询）
     */
    Page<Product> findByNameContaining(String name, Pageable pageable);
}
