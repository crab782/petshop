package com.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petshop.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {

    /**
     * 根据ID查询预约，同时加载所有关联信息（用户、服务、商家、宠物）
     *
     * @param id 预约ID
     * @return 包含所有关联信息的预约对象
     */
    Appointment selectByIdWithRelations(@Param("id") Integer id);
}
