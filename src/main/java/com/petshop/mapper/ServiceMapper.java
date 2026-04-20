package com.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petshop.entity.Service;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ServiceMapper extends BaseMapper<Service> {

    /**
     * 根据ID查询服务，同时加载商家信息
     *
     * @param id 服务ID
     * @return 包含商家信息的服务对象
     */
    Service selectByIdWithMerchant(@Param("id") Integer id);
}
