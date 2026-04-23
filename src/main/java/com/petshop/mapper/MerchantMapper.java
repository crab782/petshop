package com.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petshop.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
    
    @Select("SELECT * FROM `merchant` WHERE email = #{email}")
    Merchant selectByEmail(@Param("email") String email);

    @Select("SELECT * FROM `merchant` WHERE phone = #{phone}")
    Merchant selectByPhone(@Param("phone") String phone);
}
