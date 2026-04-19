package com.petshop.repository;

import com.petshop.entity.SearchHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Integer> {
    
    List<SearchHistory> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    
    @Query("SELECT DISTINCT sh.keyword FROM SearchHistory sh WHERE sh.user.id = :userId ORDER BY sh.createdAt DESC")
    List<String> findDistinctKeywordsByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId, Pageable pageable);
    
    @Modifying
    @Query("DELETE FROM SearchHistory sh WHERE sh.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT sh.keyword as keyword, COUNT(sh) as count FROM SearchHistory sh GROUP BY sh.keyword ORDER BY COUNT(sh) DESC")
    List<Object[]> findHotKeywords(Pageable pageable);
    
    boolean existsByUserIdAndKeyword(Integer userId, String keyword);
    
    @Modifying
    @Query("DELETE FROM SearchHistory sh WHERE sh.user.id = :userId AND sh.keyword = :keyword")
    void deleteByUserIdAndKeyword(@Param("userId") Integer userId, @Param("keyword") String keyword);
    
    long countByUserId(Integer userId);
    
    @Modifying
    @Query("DELETE FROM SearchHistory sh WHERE sh.user.id = :userId AND sh.id NOT IN :ids")
    void deleteAllByIdNotInAndUserId(@Param("ids") List<Integer> ids, @Param("userId") Integer userId);
}
