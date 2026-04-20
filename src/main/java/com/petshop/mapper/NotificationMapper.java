package com.petshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petshop.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    
    @Update("UPDATE notification SET is_read = true WHERE user_id = #{userId}")
    int markAllAsReadByUserId(Integer userId);
    
    @Update("<script>" +
            "UPDATE notification SET is_read = true WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND user_id = #{userId}" +
            "</script>")
    int markAsReadByIds(List<Integer> ids, Integer userId);
}
