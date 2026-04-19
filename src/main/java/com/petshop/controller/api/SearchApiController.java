package com.petshop.controller.api;

import com.petshop.dto.HotKeywordDTO;
import com.petshop.dto.SearchSuggestionsDTO;
import com.petshop.entity.User;
import com.petshop.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "搜索API", description = "搜索相关接口")
public class SearchApiController {
    
    @Autowired
    private SearchService searchService;
    
    @Operation(summary = "获取搜索建议", description = "根据关键字获取服务、商品、商家的搜索建议")
    @GetMapping("/search/suggestions")
    public ResponseEntity<SearchSuggestionsDTO> getSearchSuggestions(@RequestParam String keyword) {
        SearchSuggestionsDTO suggestions = searchService.getSearchSuggestions(keyword);
        return ResponseEntity.ok(suggestions);
    }
    
    @Operation(summary = "获取热门搜索关键字", description = "获取热门搜索关键字列表")
    @GetMapping("/search/hot-keywords")
    public ResponseEntity<List<HotKeywordDTO>> getHotKeywords(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<HotKeywordDTO> hotKeywords = searchService.getHotKeywords(limit);
        return ResponseEntity.ok(hotKeywords);
    }
    
    @Operation(summary = "保存搜索历史", description = "保存用户搜索关键字到历史记录")
    @PostMapping("/user/search-history")
    public ResponseEntity<Void> saveSearchHistory(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String keyword = request.get("keyword");
        if (keyword != null && !keyword.trim().isEmpty()) {
            searchService.saveSearchHistory(user.getId(), keyword);
        }
        
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "获取搜索历史", description = "获取用户的搜索历史记录")
    @GetMapping("/user/search-history")
    public ResponseEntity<List<String>> getSearchHistory(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<String> history = searchService.getSearchHistory(user.getId(), limit);
        return ResponseEntity.ok(history);
    }
    
    @Operation(summary = "清空搜索历史", description = "清空用户的所有搜索历史记录")
    @DeleteMapping("/user/search-history")
    public ResponseEntity<Void> clearSearchHistory(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        searchService.clearSearchHistory(user.getId());
        return ResponseEntity.noContent().build();
    }
}
