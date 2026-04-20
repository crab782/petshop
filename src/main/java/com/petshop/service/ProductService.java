package com.petshop.service;

import com.petshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    Product getProductById(Long id);

    boolean reduceStock(Long productId, int quantity);

    Product create(Product product);

    Product findById(Integer id);

    /**
     * 根据ID查询商品，同时加载商家信息
     *
     * @param id 商品ID
     * @return 包含商家信息的商品对象
     */
    Product findByIdWithMerchant(Integer id);

    List<Product> findByMerchantId(Integer merchantId);

    Page<Product> findByMerchantId(Integer merchantId, Pageable pageable);

    List<Product> findByMerchantIdAndStatus(Integer merchantId, String status);

    Page<Product> findByMerchantIdAndStatus(Integer merchantId, String status, Pageable pageable);

    List<Product> findByMerchantIdAndCategory(Integer merchantId, String category);

    Page<Product> findByMerchantIdAndCategory(Integer merchantId, String category, Pageable pageable);

    List<Product> findByStatus(String status);

    Page<Product> findByStatus(String status, Pageable pageable);

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    Page<Product> searchProducts(Integer merchantId, String name, String status, String category, Pageable pageable);

    Page<Product> searchAllProducts(String name, String status, String category, Pageable pageable);

    Product update(Product product);

    void delete(Integer id);

    void batchDelete(List<Integer> ids);

    int batchUpdateStatus(List<Integer> ids, String status);

    List<Product> findLowStockProducts(Integer merchantId);

    List<Product> findAllLowStockProducts();

    int updateStock(Integer id, Integer quantity);

    Product setStock(Integer id, Integer stock);

    long countByMerchantId(Integer merchantId);

    long countByMerchantIdAndStatus(Integer merchantId, String status);

    long countLowStockProducts(Integer merchantId);

    Product enableProduct(Integer id);

    Product disableProduct(Integer id);

    Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Product> adminSearchProducts(String keyword, String status, Integer merchantId, String category, Pageable pageable);

    List<Product> searchProducts(
            String name, Double minPrice, Double maxPrice, 
            Integer minStock, String status, Integer categoryId, 
            int page, int pageSize);

    List<Product> searchProducts(
            String keyword, Integer merchantId, 
            String sortBy, String sortOrder, 
            int page, int pageSize);
}