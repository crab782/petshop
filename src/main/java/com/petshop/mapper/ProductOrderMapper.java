package com.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petshop.entity.ProductOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductOrderMapper extends BaseMapper<ProductOrder> {

    /**
     * 根据ID查询订单，同时加载商家信息
     *
     * @param id 订单ID
     * @return 包含商家信息的订单对象
     */
    ProductOrder selectByIdWithMerchant(@Param("id") Integer id);
}
