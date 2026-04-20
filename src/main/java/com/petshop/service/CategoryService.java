package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.entity.Category;
import com.petshop.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public Category create(Category category) {
        Category existing = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getMerchantId, category.getMerchantId())
                .eq(Category::getName, category.getName()));
        if (existing != null) {
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
        categoryMapper.insert(category);
        return category;
    }

    public Category update(Category category) {
        Category existingCategory = categoryMapper.selectById(category.getId());
        if (existingCategory == null) {
            throw new RuntimeException("分类不存在");
        }
        
        if (!existingCategory.getName().equals(category.getName())) {
            Category duplicate = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                    .eq(Category::getMerchantId, category.getMerchantId())
                    .eq(Category::getName, category.getName()));
            if (duplicate != null) {
                throw new RuntimeException("分类名称已存在");
            }
        }
        
        existingCategory.setName(category.getName());
        existingCategory.setIcon(category.getIcon());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setSort(category.getSort());
        existingCategory.setUpdatedAt(LocalDateTime.now());
        
        categoryMapper.updateById(existingCategory);
        return existingCategory;
    }

    public Category findById(Integer id) {
        return categoryMapper.selectById(id);
    }

    public List<Category> findByMerchantId(Integer merchantId) {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getMerchantId, merchantId)
                .orderByAsc(Category::getSort));
    }

    public List<Category> findByMerchantIdSorted(Integer merchantId) {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getMerchantId, merchantId)
                .orderByAsc(Category::getSort));
    }

    public List<Category> findByMerchantIdAndStatus(Integer merchantId, String status) {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getMerchantId, merchantId)
                .eq(Category::getStatus, status));
    }

    public List<Category> findAll() {
        return categoryMapper.selectList(null);
    }

    @Transactional
    public void delete(Integer id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        if (category.getProductCount() > 0) {
            throw new RuntimeException("该分类下存在商品，无法删除");
        }
        categoryMapper.deleteById(id);
    }

    @Transactional
    public Category toggleStatus(Integer id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        String newStatus = "enabled".equals(category.getStatus()) ? "disabled" : "enabled";
        category.setStatus(newStatus);
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.updateById(category);
        return category;
    }

    @Transactional
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            Category category = categoryMapper.selectById(id);
            if (category == null) {
                throw new RuntimeException("分类 ID " + id + " 不存在");
            }
            if (category.getProductCount() > 0) {
                throw new RuntimeException("分类 " + category.getName() + " 下存在商品，无法删除");
            }
        }
        for (Integer id : ids) {
            categoryMapper.deleteById(id);
        }
    }

    @Transactional
    public int batchEnable(List<Integer> ids) {
        int count = 0;
        for (Integer id : ids) {
            Category category = categoryMapper.selectById(id);
            if (category != null) {
                category.setStatus("enabled");
                categoryMapper.updateById(category);
                count++;
            }
        }
        return count;
    }

    @Transactional
    public int batchDisable(List<Integer> ids) {
        int count = 0;
        for (Integer id : ids) {
            Category category = categoryMapper.selectById(id);
            if (category != null) {
                category.setStatus("disabled");
                categoryMapper.updateById(category);
                count++;
            }
        }
        return count;
    }

    @Transactional
    public void incrementProductCount(Integer categoryId) {
        categoryMapper.incrementProductCount(categoryId);
    }

    @Transactional
    public void decrementProductCount(Integer categoryId) {
        categoryMapper.decrementProductCount(categoryId);
    }

    public long countByMerchantIdAndStatus(Integer merchantId, String status) {
        return categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                .eq(Category::getMerchantId, merchantId)
                .eq(Category::getStatus, status));
    }
}
