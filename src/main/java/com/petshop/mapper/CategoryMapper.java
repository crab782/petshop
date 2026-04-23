package com.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petshop.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    
    @Update("UPDATE `category` SET product_count = product_count + 1 WHERE id = #{categoryId}")
    void incrementProductCount(Integer categoryId);
    
    @Update("UPDATE `category` SET product_count = product_count - 1 WHERE id = #{categoryId} AND product_count > 0")
    void decrementProductCount(Integer categoryId);
}
