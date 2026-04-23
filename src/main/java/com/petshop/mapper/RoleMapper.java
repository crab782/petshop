package com.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petshop.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    
    @Select("SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM `role` WHERE id = #{id}")
    boolean existsById(@Param("id") Integer id);
}
