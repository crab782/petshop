package com.petshop.service;

import com.petshop.entity.Product;
import com.petshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public Product create(Product product) {
        if (product.getStatus() == null) {
            product.setStatus("enabled");
        }
        if (product.getLowStockThreshold() == null) {
            product.setLowStockThreshold(10);
        }
        return productRepository.save(product);
    }
    
    public Product findById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }
    
    public List<Product> findByMerchantId(Integer merchantId) {
        return productRepository.findByMerchantId(merchantId);
    }
    
    public Page<Product> findByMerchantId(Integer merchantId, Pageable pageable) {
        return productRepository.findByMerchantId(merchantId, pageable);
    }
    
    public List<Product> findByMerchantIdAndStatus(Integer merchantId, String status) {
        return productRepository.findByMerchantIdAndStatus(merchantId, status);
    }
    
    public Page<Product> findByMerchantIdAndStatus(Integer merchantId, String status, Pageable pageable) {
        return productRepository.findByMerchantIdAndStatus(merchantId, status, pageable);
    }
    
    public List<Product> findByMerchantIdAndCategory(Integer merchantId, String category) {
        return productRepository.findByMerchantIdAndCategory(merchantId, category);
    }
    
    public Page<Product> findByMerchantIdAndCategory(Integer merchantId, String category, Pageable pageable) {
        return productRepository.findByMerchantIdAndCategory(merchantId, category, pageable);
    }
    
    public List<Product> findByStatus(String status) {
        return productRepository.findByStatus(status);
    }
    
    public Page<Product> findByStatus(String status, Pageable pageable) {
        return productRepository.findByStatus(status, pageable);
    }
    
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    public Page<Product> searchProducts(Integer merchantId, String name, String status, String category, Pageable pageable) {
        return productRepository.searchProducts(merchantId, name, status, category, pageable);
    }
    
    public Page<Product> searchAllProducts(String name, String status, String category, Pageable pageable) {
        return productRepository.searchAllProducts(name, status, category, pageable);
    }
    
    public Product update(Product product) {
        Product existingProduct = productRepository.findById(product.getId()).orElse(null);
        if (existingProduct == null) {
            throw new RuntimeException("Product not found with id: " + product.getId());
        }
        return productRepository.save(product);
    }
    
    public void delete(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
    
    @Transactional
    public void batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Product IDs cannot be null or empty");
        }
        productRepository.batchDelete(ids);
    }
    
    @Transactional
    public int batchUpdateStatus(List<Integer> ids, String status) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Product IDs cannot be null or empty");
        }
        if (!"enabled".equals(status) && !"disabled".equals(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        return productRepository.batchUpdateStatus(ids, status);
    }
    
    public List<Product> findLowStockProducts(Integer merchantId) {
        return productRepository.findLowStockProducts(merchantId);
    }
    
    public List<Product> findAllLowStockProducts() {
        return productRepository.findAllLowStockProducts();
    }
    
    @Transactional
    public int updateStock(Integer id, Integer quantity) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("Insufficient stock. Current stock: " + product.getStock() + ", requested: " + Math.abs(quantity));
        }
        return productRepository.updateStock(id, quantity);
    }
    
    @Transactional
    public Product setStock(Integer id, Integer stock) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        product.setStock(stock);
        return productRepository.save(product);
    }
    
    public long countByMerchantId(Integer merchantId) {
        return productRepository.countByMerchantId(merchantId);
    }
    
    public long countByMerchantIdAndStatus(Integer merchantId, String status) {
        return productRepository.countByMerchantIdAndStatus(merchantId, status);
    }
    
    public long countLowStockProducts(Integer merchantId) {
        return productRepository.countLowStockProducts(merchantId);
    }
    
    public Product enableProduct(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        product.setStatus("enabled");
        return productRepository.save(product);
    }
    
    public Product disableProduct(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        product.setStatus("disabled");
        return productRepository.save(product);
    }
    
    public Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        return productRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    public Page<Product> adminSearchProducts(String keyword, String status, Integer merchantId, String category, Pageable pageable) {
        return productRepository.adminSearchProducts(keyword, status, merchantId, category, pageable);
    }

    public List<Product> searchProducts(
            String name, Double minPrice, Double maxPrice, 
            Integer minStock, String status, Integer categoryId, 
            int page, int pageSize) {
        // 这里需要实现商品搜索的业务逻辑
        // 暂时返回所有启用的商品，实际实现需要根据参数进行筛选
        return productRepository.findByStatus("enabled");
    }

    public List<Product> searchProducts(
            String keyword, Integer merchantId, 
            String sortBy, String sortOrder, 
            int page, int pageSize) {
        // 这里需要实现商品搜索的业务逻辑
        // 暂时返回所有启用的商品，实际实现需要根据参数进行筛选和排序
        return productRepository.findByStatus("enabled");
    }
}
