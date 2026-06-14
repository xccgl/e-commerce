package com.example.ecommerce.service;

import com.example.ecommerce.dto.PageResult;
import com.example.ecommerce.dto.ProductRequest;
import com.example.ecommerce.entity.Product;

/**
 * 商品服务接口
 */
public interface ProductService {

    /**
     * 添加商品
     */
    Product addProduct(ProductRequest request);

    /**
     * 删除商品
     */
    void deleteProduct(Long id);

    /**
     * 更新商品
     */
    Product updateProduct(Long id, ProductRequest request);

    /**
     * 根据ID查询商品
     */
    Product getProductById(Long id);

    /**
     * 分页查询商品列表
     */
    PageResult<Product> queryProducts(int page, int size, String name, String category);
}
