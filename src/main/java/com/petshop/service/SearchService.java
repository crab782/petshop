package com.petshop.service;

import com.petshop.dto.HotKeywordDTO;
import com.petshop.dto.MerchantDTO;
import com.petshop.dto.ProductDTO;
import com.petshop.dto.SearchSuggestionsDTO;
import com.petshop.dto.ServiceDTO;
import com.petshop.entity.Merchant;
import com.petshop.entity.Product;
import com.petshop.entity.SearchHistory;
import com.petshop.repository.MerchantRepository;
import com.petshop.repository.ProductRepository;
import com.petshop.repository.ReviewRepository;
import com.petshop.repository.SearchHistoryRepository;
import com.petshop.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class SearchService {
    
    private static final int MAX_SEARCH_HISTORY = 20;
    private static final int DEFAULT_SUGGESTION_LIMIT = 5;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MerchantRepository merchantRepository;
    
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    public SearchSuggestionsDTO getSearchSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return SearchSuggestionsDTO.builder()
                    .services(new ArrayList<>())
                    .products(new ArrayList<>())
                    .merchants(new ArrayList<>())
                    .build();
        }
        
        String trimmedKeyword = keyword.trim();
        Pageable limit = PageRequest.of(0, DEFAULT_SUGGESTION_LIMIT);
        
        List<com.petshop.entity.Service> services = serviceRepository.searchByKeyword(trimmedKeyword);
        List<Product> products = productRepository.searchByKeyword(trimmedKeyword, limit).getContent();
        List<Merchant> merchants = merchantRepository.searchByKeyword(trimmedKeyword, limit).getContent();
        
        List<ServiceDTO> serviceDTOs = services.stream()
                .limit(DEFAULT_SUGGESTION_LIMIT)
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
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = searchHistoryRepository.findHotKeywords(pageable);
        
        return results.stream()
                .map(result -> HotKeywordDTO.builder()
                        .keyword((String) result[0])
                        .count((Long) result[1])
                        .build())
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void saveSearchHistory(Integer userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        
        String trimmedKeyword = keyword.trim();
        
        searchHistoryRepository.deleteByUserIdAndKeyword(userId, trimmedKeyword);
        
        SearchHistory searchHistory = new SearchHistory();
        com.petshop.entity.User user = new com.petshop.entity.User();
        user.setId(userId);
        searchHistory.setUser(user);
        searchHistory.setKeyword(trimmedKeyword);
        searchHistoryRepository.save(searchHistory);
        
        long count = searchHistoryRepository.countByUserId(userId);
        if (count > MAX_SEARCH_HISTORY) {
            cleanOldSearchHistory(userId);
        }
    }
    
    public List<String> getSearchHistory(Integer userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        Pageable pageable = PageRequest.of(0, limit);
        return searchHistoryRepository.findDistinctKeywordsByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    @Transactional
    public void clearSearchHistory(Integer userId) {
        searchHistoryRepository.deleteByUserId(userId);
    }
    
    @Transactional
    protected void cleanOldSearchHistory(Integer userId) {
        Pageable pageable = PageRequest.of(0, MAX_SEARCH_HISTORY);
        List<SearchHistory> recentHistory = searchHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        List<Integer> idsToKeep = recentHistory.stream()
                .map(SearchHistory::getId)
                .collect(Collectors.toList());
        
        if (!idsToKeep.isEmpty()) {
            searchHistoryRepository.deleteAllByIdNotInAndUserId(idsToKeep, userId);
        }
    }
    
    private ServiceDTO convertToServiceDTO(com.petshop.entity.Service service) {
        return ServiceDTO.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .price(service.getPrice())
                .duration(service.getDuration())
                .merchantId(service.getMerchant() != null ? service.getMerchant().getId() : null)
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
                .merchantId(product.getMerchant() != null ? product.getMerchant().getId() : null)
                .image(product.getImage())
                .status(product.getStatus())
                .category(product.getCategory())
                .build();
    }
    
    private MerchantDTO convertToMerchantDTO(Merchant merchant) {
        Double rating = reviewRepository.getAverageRatingByMerchantId(merchant.getId());
        return MerchantDTO.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .logo(merchant.getLogo())
                .contact(merchant.getContactPerson())
                .phone(merchant.getPhone())
                .address(merchant.getAddress())
                .rating(rating != null ? Math.round(rating * 10.0) / 10.0 : 0.0)
                .build();
    }
}
