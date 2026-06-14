package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.PageResult;
import com.example.ecommerce.dto.ProductRequest;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Product addProduct(ProductRequest request) {
        log.info("添加商品: {}", request.getName());
        
        // 检查商品是否已存在
        if (productRepository.existsByName(request.getName())) {
            throw new BusinessException(400, "商品已存在: " + request.getName());
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        Product savedProduct = productRepository.save(product);
        log.info("商品添加成功，ID: {}", savedProduct.getId());
        return savedProduct;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.info("删除商品，ID: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new BusinessException(404, "商品不存在: " + id);
        }

        productRepository.deleteById(id);
        log.info("商品删除成功，ID: {}", id);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        log.info("更新商品，ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "商品不存在: " + id));

        // 如果名称变更，检查是否与其他商品重名
        if (!product.getName().equals(request.getName()) 
                && productRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BusinessException(400, "商品名称已存在: " + request.getName());
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        Product updatedProduct = productRepository.save(product);
        log.info("商品更新成功，ID: {}", updatedProduct.getId());
        return updatedProduct;
    }

    @Override
    public Product getProductById(Long id) {
        log.info("查询商品，ID: {}", id);
        
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "商品不存在: " + id));
    }

    @Override
    public PageResult<Product> queryProducts(int page, int size, String name, String category) {
        log.info("分页查询商品，page: {}, size: {}, name: {}, category: {}", 
                page, size, name, category);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> productPage;

        // 根据条件查询
        if (name != null && !name.isEmpty()) {
            productPage = productRepository.findByNameContaining(name, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        return PageResult.of(
                productPage.getContent(),
                page,
                size,
                productPage.getTotalElements()
        );
    }
}
