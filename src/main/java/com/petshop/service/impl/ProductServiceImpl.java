package com.petshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.entity.Product;
import com.petshop.mapper.ProductMapper;
import com.petshop.service.ProductService;
import com.petshop.util.RedisLockUtil;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private RedisLockUtil redisLockUtil;

    @Override
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean reduceStock(Long productId, int quantity) {
        String lockKey = "stock:lock:" + productId;
        String requestId = UUID.randomUUID().toString();
        long expireTime = 5000;
        long timeout = 3000;

        try {
            if (!redisLockUtil.lock(lockKey, requestId, expireTime, timeout)) {
                throw new RuntimeException("获取锁失败，可能有其他操作正在进行");
            }

            Product product = productMapper.selectById(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }

            if (product.getStock() < quantity) {
                throw new RuntimeException("库存不足");
            }

            product.setStock(product.getStock() - quantity);
            productMapper.updateById(product);

            return true;
        } finally {
            redisLockUtil.releaseLock(lockKey, requestId);
        }
    }

    @Override
    public Product create(Product product) {
        productMapper.insert(product);
        return product;
    }

    @Override
    public Product findById(Integer id) {
        return productMapper.selectById(id);
    }

    @Override
    public Product findByIdWithMerchant(Integer id) {
        return productMapper.selectByIdWithMerchant(id);
    }

    @Override
    public List<Product> findByMerchantId(Integer merchantId) {
        return productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId));
    }

    @Override
    public org.springframework.data.domain.Page<Product> findByMerchantId(Integer merchantId, Pageable pageable) {
        Page<Product> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId);
        Page<Product> result = productMapper.selectPage(page, wrapper);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public List<Product> findByMerchantIdAndStatus(Integer merchantId, String status) {
        return productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId)
                .eq(Product::getStatus, status));
    }

    @Override
    public org.springframework.data.domain.Page<Product> findByMerchantIdAndStatus(Integer merchantId, String status, Pageable pageable) {
        Page<Product> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId)
                .eq(Product::getStatus, status);
        Page<Product> result = productMapper.selectPage(page, wrapper);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public List<Product> findByMerchantIdAndCategory(Integer merchantId, String category) {
        return productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId)
                .eq(Product::getCategory, category));
    }

    @Override
    public org.springframework.data.domain.Page<Product> findByMerchantIdAndCategory(Integer merchantId, String category, Pageable pageable) {
        Page<Product> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId)
                .eq(Product::getCategory, category);
        Page<Product> result = productMapper.selectPage(page, wrapper);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public List<Product> findByStatus(String status) {
        return productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, status));
    }

    @Override
    public org.springframework.data.domain.Page<Product> findByStatus(String status, Pageable pageable) {
        Page<Product> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, status);
        Page<Product> result = productMapper.selectPage(page, wrapper);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public List<Product> findAll() {
        return productMapper.selectList(null);
    }

    @Override
    public org.springframework.data.domain.Page<Product> findAll(Pageable pageable) {
        Page<Product> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        Page<Product> result = productMapper.selectPage(page, null);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public org.springframework.data.domain.Page<Product> searchProducts(Integer merchantId, String name, String status, String category, Pageable pageable) {
        Page<Product> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId);
        if (name != null && !name.isEmpty()) {
            wrapper.like(Product::getName, name);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Product::getStatus, status);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Product::getCategory, category);
        }
        Page<Product> result = productMapper.selectPage(page, wrapper);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public org.springframework.data.domain.Page<Product> searchAllProducts(String name, String status, String category, Pageable pageable) {
        Page<Product> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Product::getName, name);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Product::getStatus, status);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Product::getCategory, category);
        }
        Page<Product> result = productMapper.selectPage(page, wrapper);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public Product update(Product product) {
        productMapper.updateById(product);
        return product;
    }

    @Override
    public void delete(Integer id) {
        productMapper.deleteById(id);
    }

    @Override
    public void batchDelete(List<Integer> ids) {
        productMapper.deleteBatchIds(ids);
    }

    @Override
    public int batchUpdateStatus(List<Integer> ids, String status) {
        int count = 0;
        for (Integer id : ids) {
            Product product = productMapper.selectById(id);
            if (product != null) {
                product.setStatus(status);
                productMapper.updateById(product);
                count++;
            }
        }
        return count;
    }

    @Override
    public List<Product> findLowStockProducts(Integer merchantId) {
        return productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId)
                .lt(Product::getStock, 10));
    }

    @Override
    public List<Product> findAllLowStockProducts() {
        return productMapper.selectList(new LambdaQueryWrapper<Product>()
                .lt(Product::getStock, 10));
    }

    @Override
    public int updateStock(Integer id, Integer quantity) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            product.setStock(product.getStock() + quantity);
            productMapper.updateById(product);
            return product.getStock();
        }
        return -1;
    }

    @Override
    public Product setStock(Integer id, Integer stock) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            product.setStock(stock);
            productMapper.updateById(product);
        }
        return product;
    }

    @Override
    public long countByMerchantId(Integer merchantId) {
        return productMapper.selectCount(new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId));
    }

    @Override
    public long countByMerchantIdAndStatus(Integer merchantId, String status) {
        return productMapper.selectCount(new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId)
                .eq(Product::getStatus, status));
    }

    @Override
    public long countLowStockProducts(Integer merchantId) {
        return productMapper.selectCount(new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId)
                .lt(Product::getStock, 10));
    }

    @Override
    public Product enableProduct(Integer id) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            product.setStatus("enabled");
            productMapper.updateById(product);
        }
        return product;
    }

    @Override
    public Product disableProduct(Integer id) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            product.setStatus("disabled");
            productMapper.updateById(product);
        }
        return product;
    }

    @Override
    public org.springframework.data.domain.Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        Page<Product> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .orderByDesc(Product::getCreatedAt);
        Page<Product> result = productMapper.selectPage(page, wrapper);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public org.springframework.data.domain.Page<Product> adminSearchProducts(String keyword, String status, Integer merchantId, String category, Pageable pageable) {
        Page<Product> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Product::getName, keyword);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Product::getStatus, status);
        }
        if (merchantId != null) {
            wrapper.eq(Product::getMerchantId, merchantId);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Product::getCategory, category);
        }
        Page<Product> result = productMapper.selectPage(page, wrapper);
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }

    @Override
    public List<Product> searchProducts(String name, Double minPrice, Double maxPrice, Integer minStock, String status, Integer categoryId, int page, int pageSize) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Product::getName, name);
        }
        if (minPrice != null) {
            wrapper.ge(Product::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(Product::getPrice, maxPrice);
        }
        if (minStock != null) {
            wrapper.ge(Product::getStock, minStock);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Product::getStatus, status);
        }
        Page<Product> pageResult = productMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return pageResult.getRecords();
    }

    @Override
    public List<Product> searchProducts(String keyword, Integer merchantId, String sortBy, String sortOrder, int page, int pageSize) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Product::getName, keyword);
        }
        if (merchantId != null) {
            wrapper.eq(Product::getMerchantId, merchantId);
        }
        if ("price".equals(sortBy)) {
            if ("desc".equalsIgnoreCase(sortOrder)) {
                wrapper.orderByDesc(Product::getPrice);
            } else {
                wrapper.orderByAsc(Product::getPrice);
            }
        } else if ("createdAt".equals(sortBy)) {
            if ("desc".equalsIgnoreCase(sortOrder)) {
                wrapper.orderByDesc(Product::getCreatedAt);
            } else {
                wrapper.orderByAsc(Product::getCreatedAt);
            }
        } else {
            wrapper.orderByDesc(Product::getCreatedAt);
        }
        Page<Product> pageResult = productMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return pageResult.getRecords();
    }
}
