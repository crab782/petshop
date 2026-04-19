package com.petshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchSuggestionsDTO {
    private List<ServiceDTO> services;
    private List<ProductDTO> products;
    private List<MerchantDTO> merchants;
}
