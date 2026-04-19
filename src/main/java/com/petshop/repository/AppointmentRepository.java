package com.petshop.repository;

import com.petshop.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByUserId(Integer userId);
    List<Appointment> findByMerchantId(Integer merchantId);
    long countByUserIdAndStatus(Integer userId, String status);
    List<Appointment> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.service s JOIN FETCH a.merchant m " +
           "WHERE a.user.id = :userId " +
           "AND (:keyword IS NULL OR s.name LIKE %:keyword%) " +
           "AND (:status IS NULL OR a.status = :status)")
    Page<Appointment> findPurchasedServices(@Param("userId") Integer userId,
                                            @Param("keyword") String keyword,
                                            @Param("status") String status,
                                            Pageable pageable);

    @Query("SELECT a FROM Appointment a " +
           "JOIN FETCH a.service s " +
           "JOIN FETCH a.merchant m " +
           "JOIN FETCH a.pet p " +
           "WHERE a.user.id = :userId " +
           "AND (:status IS NULL OR a.status = :status) " +
           "AND (:keyword IS NULL OR s.name LIKE %:keyword% OR m.name LIKE %:keyword%) " +
           "AND (:startDate IS NULL OR a.appointmentTime >= :startDate) " +
           "AND (:endDate IS NULL OR a.appointmentTime <= :endDate)")
    Page<Appointment> findByUserIdWithFilters(
            @Param("userId") Integer userId,
            @Param("status") String status,
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT a FROM Appointment a " +
           "JOIN FETCH a.service s " +
           "JOIN FETCH a.merchant m " +
           "JOIN FETCH a.pet p " +
           "WHERE a.id = :id AND a.user.id = :userId")
    Optional<Appointment> findByIdAndUserIdWithDetails(@Param("id") Integer id, @Param("userId") Integer userId);

    long countByUserId(Integer userId);
    long countByUserIdAndStatusIn(Integer userId, List<String> statuses);
}
