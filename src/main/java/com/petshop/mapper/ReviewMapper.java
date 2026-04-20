package com.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petshop.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    
    @Select("SELECT AVG(rating) FROM review WHERE merchant_id = #{merchantId}")
    Double getAverageRatingByMerchantId(@Param("merchantId") Integer merchantId);
}
