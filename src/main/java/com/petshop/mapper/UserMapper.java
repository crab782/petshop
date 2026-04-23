package com.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petshop.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM `user` WHERE email = #{email}")
    User selectByEmail(@Param("email") String email);
    
    @Select("SELECT * FROM `user` WHERE phone = #{phone}")
    User selectByPhone(@Param("phone") String phone);
}
