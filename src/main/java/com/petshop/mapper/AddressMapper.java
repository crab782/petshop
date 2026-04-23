package com.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petshop.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {
    
    @Update("UPDATE `address` SET is_default = false WHERE user_id = #{userId}")
    int clearDefaultByUserId(Integer userId);
}
