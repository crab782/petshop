package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.dto.HotKeywordDTO;
import com.petshop.dto.MerchantDTO;
import com.petshop.dto.ProductDTO;
import com.petshop.dto.SearchSuggestionsDTO;
import com.petshop.dto.ServiceDTO;
import com.petshop.entity.Merchant;
import com.petshop.entity.Product;
import com.petshop.entity.SearchHistory;
import com.petshop.mapper.MerchantMapper;
import com.petshop.mapper.ProductMapper;
import com.petshop.mapper.ReviewMapper;
import com.petshop.mapper.SearchHistoryMapper;
import com.petshop.mapper.ServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    
    private static final int MAX_SEARCH_HISTORY = 20;
    private static final int DEFAULT_SUGGESTION_LIMIT = 5;
    
    @Autowired
    private ServiceMapper serviceMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private MerchantMapper merchantMapper;
    
    @Autowired
    private SearchHistoryMapper searchHistoryMapper;
    
    @Autowired
    private ReviewMapper reviewMapper;
    
    public SearchSuggestionsDTO getSearchSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return SearchSuggestionsDTO.builder()
                    .services(new ArrayList<>())
                    .products(new ArrayList<>())
                    .merchants(new ArrayList<>())
                    .build();
        }
        
        String trimmedKeyword = keyword.trim();
        
        LambdaQueryWrapper<com.petshop.entity.Service> serviceWrapper = new LambdaQueryWrapper<>();
        serviceWrapper.like(com.petshop.entity.Service::getName, trimmedKeyword)
                     .or().like(com.petshop.entity.Service::getDescription, trimmedKeyword)
                     .last("LIMIT " + DEFAULT_SUGGESTION_LIMIT);
        List<com.petshop.entity.Service> services = serviceMapper.selectList(serviceWrapper);
        
        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.like(Product::getName, trimmedKeyword)
                     .or().like(Product::getDescription, trimmedKeyword)
                     .last("LIMIT " + DEFAULT_SUGGESTION_LIMIT);
        Page<Product> productPage = new Page<>(1, DEFAULT_SUGGESTION_LIMIT);
        List<Product> products = productMapper.selectPage(productPage, productWrapper).getRecords();
        
        LambdaQueryWrapper<Merchant> merchantWrapper = new LambdaQueryWrapper<>();
        merchantWrapper.like(Merchant::getName, trimmedKeyword)
                      .last("LIMIT " + DEFAULT_SUGGESTION_LIMIT);
        Page<Merchant> merchantPage = new Page<>(1, DEFAULT_SUGGESTION_LIMIT);
        List<Merchant> merchants = merchantMapper.selectPage(merchantPage, merchantWrapper).getRecords();
        
        List<ServiceDTO> serviceDTOs = services.stream()
                .map(this::convertToServiceDTO)
                .collect(Collectors.toList());
        
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
        
        List<MerchantDTO> merchantDTOs = merchants.stream()
                .map(this::convertToMerchantDTO)
                .collect(Collectors.toList());
        
        return SearchSuggestionsDTO.builder()
                .services(serviceDTOs)
                .products(productDTOs)
                .merchants(merchantDTOs)
                .build();
    }
    
    public List<HotKeywordDTO> getHotKeywords(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SearchHistory::getKeyword)
               .groupBy(SearchHistory::getKeyword)
               .orderByDesc(SearchHistory::getKeyword);
        
        Page<SearchHistory> page = new Page<>(1, limit);
        List<SearchHistory> results = searchHistoryMapper.selectPage(page, wrapper).getRecords();
        
        return results.stream()
                .map(result -> HotKeywordDTO.builder()
                        .keyword(result.getKeyword())
                        .count(1L)
                        .build())
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void saveSearchHistory(Integer userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        
        String trimmedKeyword = keyword.trim();
        
        LambdaQueryWrapper<SearchHistory> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SearchHistory::getUserId, userId)
                    .eq(SearchHistory::getKeyword, trimmedKeyword);
        searchHistoryMapper.delete(deleteWrapper);
        
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setUserId(userId);
        searchHistory.setKeyword(trimmedKeyword);
        searchHistoryMapper.insert(searchHistory);
        
        LambdaQueryWrapper<SearchHistory> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(SearchHistory::getUserId, userId);
        long count = searchHistoryMapper.selectCount(countWrapper);
        
        if (count > MAX_SEARCH_HISTORY) {
            cleanOldSearchHistory(userId);
        }
    }
    
    public List<String> getSearchHistory(Integer userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SearchHistory::getKeyword)
               .eq(SearchHistory::getUserId, userId)
               .groupBy(SearchHistory::getKeyword)
               .orderByDesc(SearchHistory::getCreatedAt)
               .last("LIMIT " + limit);
        
        List<SearchHistory> results = searchHistoryMapper.selectList(wrapper);
        return results.stream()
                .map(SearchHistory::getKeyword)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void clearSearchHistory(Integer userId) {
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId);
        searchHistoryMapper.delete(wrapper);
    }
    
    @Transactional
    protected void cleanOldSearchHistory(Integer userId) {
        LambdaQueryWrapper<SearchHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SearchHistory::getUserId, userId)
                   .orderByDesc(SearchHistory::getCreatedAt)
                   .last("LIMIT " + MAX_SEARCH_HISTORY);
        
        List<SearchHistory> recentHistory = searchHistoryMapper.selectList(queryWrapper);
        
        List<Integer> idsToKeep = recentHistory.stream()
                .map(SearchHistory::getId)
                .collect(Collectors.toList());
        
        if (!idsToKeep.isEmpty()) {
            LambdaQueryWrapper<SearchHistory> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(SearchHistory::getUserId, userId)
                        .notIn(SearchHistory::getId, idsToKeep);
            searchHistoryMapper.delete(deleteWrapper);
        }
    }
    
    private ServiceDTO convertToServiceDTO(com.petshop.entity.Service service) {
        return ServiceDTO.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .price(service.getPrice())
                .duration(service.getDuration())
                .merchantId(service.getMerchantId() != null ? service.getMerchantId() : null)
                .merchantName(service.getMerchant() != null ? service.getMerchant().getName() : null)
                .image(service.getImage())
                .status(service.getStatus())
                .build();
    }
    
    private ProductDTO convertToProductDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .merchantId(product.getMerchantId() != null ? product.getMerchantId() : null)
                .image(product.getImage())
                .status(product.getStatus())
                .category(product.getCategory())
                .build();
    }
    
    private MerchantDTO convertToMerchantDTO(Merchant merchant) {
        LambdaQueryWrapper<com.petshop.entity.Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.petshop.entity.Review::getMerchantId, merchant.getId());
        List<com.petshop.entity.Review> reviews = reviewMapper.selectList(wrapper);
        
        Double rating = 0.0;
        if (!reviews.isEmpty()) {
            double sum = reviews.stream()
                    .mapToInt(com.petshop.entity.Review::getRating)
                    .sum();
            rating = Math.round((sum / reviews.size()) * 10.0) / 10.0;
        }
        
        return MerchantDTO.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .logo(merchant.getLogo())
                .contact(merchant.getContactPerson())
                .phone(merchant.getPhone())
                .address(merchant.getAddress())
                .rating(rating)
                .build();
    }
}
