package com.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petshop.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 根据ID查询商品，同时加载商家信息
     *
     * @param id 商品ID
     * @return 包含商家信息的商品对象
     */
    Product selectByIdWithMerchant(@Param("id") Integer id);
}
