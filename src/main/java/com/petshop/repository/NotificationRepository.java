package com.petshop.repository;

import com.petshop.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);

    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Integer userId, String type);

    List<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(Integer userId, Boolean isRead);

    List<Notification> findByUserIdAndTypeAndIsReadOrderByCreatedAtDesc(Integer userId, String type, Boolean isRead);

    long countByUserIdAndIsRead(Integer userId, Boolean isRead);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId")
    void markAllAsReadByUserId(@Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id IN :ids AND n.userId = :userId")
    void markAsReadByIds(@Param("ids") List<Integer> ids, @Param("userId") Integer userId);

    void deleteByUserIdAndIdIn(Integer userId, List<Integer> ids);
}
