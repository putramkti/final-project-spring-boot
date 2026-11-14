package com.bootcamp19.product_service.service;

import com.bootcamp19.product_service.dto.request.ProductBatchImportDto;
import com.bootcamp19.product_service.dto.request.ProductRequestDto;
import com.bootcamp19.product_service.dto.response.ProductResponseDto;

import java.util.List;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto request, String userId);
    ProductResponseDto getProductById(Long id);
    List<ProductResponseDto> getProducts(String letter);
    ProductResponseDto updateProduct(Long id, ProductRequestDto request);
    void deleteProduct(Long id);
    int importProducts(List<ProductBatchImportDto> batch);
}