package com.petshop.service;

import com.petshop.entity.Category;
import com.petshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Category create(Category category) {
        if (categoryRepository.existsByMerchantIdAndName(category.getMerchantId(), category.getName())) {
            throw new RuntimeException("分类名称已存在");
        }
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        if (category.getStatus() == null) {
            category.setStatus("enabled");
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getProductCount() == null) {
            category.setProductCount(0);
        }
        return categoryRepository.save(category);
    }

    public Category update(Category category) {
        Category existingCategory = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        if (!existingCategory.getName().equals(category.getName())) {
            if (categoryRepository.existsByMerchantIdAndName(category.getMerchantId(), category.getName())) {
                throw new RuntimeException("分类名称已存在");
            }
        }
        
        existingCategory.setName(category.getName());
        existingCategory.setIcon(category.getIcon());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setSort(category.getSort());
        existingCategory.setUpdatedAt(LocalDateTime.now());
        
        return categoryRepository.save(existingCategory);
    }

    public Category findById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public List<Category> findByMerchantId(Integer merchantId) {
        return categoryRepository.findByMerchantIdOrderBySortAsc(merchantId);
    }

    public List<Category> findByMerchantIdSorted(Integer merchantId) {
        return categoryRepository.findByMerchantIdSorted(merchantId);
    }

    public List<Category> findByMerchantIdAndStatus(Integer merchantId, String status) {
        return categoryRepository.findByMerchantIdAndStatus(merchantId, status);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional
    public void delete(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        if (category.getProductCount() > 0) {
            throw new RuntimeException("该分类下存在商品，无法删除");
        }
        categoryRepository.deleteById(id);
    }

    @Transactional
    public Category toggleStatus(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        String newStatus = "enabled".equals(category.getStatus()) ? "disabled" : "enabled";
        category.setStatus(newStatus);
        category.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    @Transactional
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("分类ID " + id + " 不存在"));
            if (category.getProductCount() > 0) {
                throw new RuntimeException("分类 " + category.getName() + " 下存在商品，无法删除");
            }
        }
        categoryRepository.deleteAllById(ids);
    }

    @Transactional
    public int batchEnable(List<Integer> ids) {
        return categoryRepository.batchUpdateStatus(ids, "enabled");
    }

    @Transactional
    public int batchDisable(List<Integer> ids) {
        return categoryRepository.batchUpdateStatus(ids, "disabled");
    }

    @Transactional
    public void incrementProductCount(Integer categoryId) {
        categoryRepository.updateProductCount(categoryId, 1);
    }

    @Transactional
    public void decrementProductCount(Integer categoryId) {
        categoryRepository.updateProductCount(categoryId, -1);
    }

    public long countByMerchantIdAndStatus(Integer merchantId, String status) {
        return categoryRepository.countByMerchantIdAndStatus(merchantId, status);
    }
}
