package com.bootcamp19.product_service.service.impl;

import com.bootcamp19.product_service.dto.request.ProductBatchImportDto;
import com.bootcamp19.product_service.dto.request.ProductRequestDto;
import com.bootcamp19.product_service.dto.response.ProductResponseDto;
import com.bootcamp19.product_service.entity.Product;
import com.bootcamp19.product_service.exception.DuplicateProductException;
import com.bootcamp19.product_service.exception.ProductNotFoundException;
import com.bootcamp19.product_service.repository.ProductRepository;
import com.bootcamp19.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public ProductResponseDto createProduct(ProductRequestDto request, String userId) {
        log.info("[ProductService] Creating product '{}' by user: {}", request.getName(), userId);

        if(productRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateProductException(request.getName());
        }

        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .category(request.getCategory())
                .brand(request.getBrand())
                .rating(request.getRating())
                .createdBy(userId != null ? userId : "ANONYMOUS")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product saved = productRepository.save(product);
        log.info("[ProductService] Product created with generated ID: {}", saved.getId());
        return ProductResponseDto.fromEntity(saved);
    }

    @Override
    @Cacheable(value = "products", key="#id")
    public ProductResponseDto getProductById(Long id) {
        log.info("[ProductService] Fetching details for product ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductResponseDto.fromEntity(product);
    }

    @Override
    public List<ProductResponseDto> getProducts(String letter) {
        List<Product> products;
        if (letter != null && !letter.isBlank()) {
            log.info("[ProductService] Fetching products containing letter: {}", letter);
            products = productRepository.findByNameContainingIgnoreCase(letter);
        } else {
            log.info("[ProductService] Fetching all products");
            products = productRepository.findAll();
        }
        return toResponses(products);
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public ProductResponseDto updateProduct(Long id, ProductRequestDto request) {
        log.info("[ProductService] Updating product ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!product.getName().equalsIgnoreCase(request.getName()) &&
                productRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateProductException(request.getName());
        }
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setRating(request.getRating());
        product.setUpdatedAt(LocalDateTime.now());

        Product updated = productRepository.save(product);
        return ProductResponseDto.fromEntity(updated);
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        log.info("[ProductService] Deleting product ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public int importProducts(List<ProductBatchImportDto> batch) {
        int importedCount = 0;
        for (ProductBatchImportDto item : batch) {
            // Rating < 3 tidak diimpor dan dicatat di log
            if (item.getRating() != null && item.getRating() < 3.0) {
                log.warn("[Import Rejected] Product '{}' skipped due to low rating: {}", item.getName(), item.getRating());
                continue;
            }

            // Pengecekan duplikat berdasarkan nama
            if (productRepository.existsByNameIgnoreCase(item.getName())) {
                log.info("[Import Skipped] Product '{}' already exists in catalog", item.getName());
                continue;
            }

            Product product = Product.builder()
                    .name(item.getName())
                    .price(item.getPrice())
                    .category(item.getCategory())
                    .brand(item.getBrand())
                    .rating(item.getRating())
                    .createdBy("SCHEDULER_IMPORT")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            productRepository.save(product);
            importedCount++;
            log.info("[Import Success] Saved product '{}' with rating {}", item.getName(), item.getRating());
        }
        return importedCount;
    }

    private List<ProductResponseDto> toResponses(List<Product> data){
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for (Product product : data) {
            productResponseDtos.add(ProductResponseDto.fromEntity(product));
        }
        return productResponseDtos;
    }
}
