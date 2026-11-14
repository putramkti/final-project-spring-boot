package com.bootcamp19.product_service.controller;

import com.bootcamp19.product_service.dto.request.ProductRequestDto;
import com.bootcamp19.product_service.dto.response.BaseResponse;
import com.bootcamp19.product_service.dto.response.ProductResponseDto;
import com.bootcamp19.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<ProductResponseDto> create(
            @Valid @RequestBody ProductRequestDto productRequestDto,
            @RequestHeader(value = "X-User-Id", required = false ) String userId) {

        log.info("Incoming create product request with X-User-Id header: {}", userId);
        return BaseResponse.ok("Product created successfully", productService.createProduct(productRequestDto, userId));
    }

    @GetMapping("/{id}")
    public BaseResponse<ProductResponseDto> getById(@PathVariable Long id) {
        return BaseResponse.ok("Product retrieved successfully", productService.getProductById(id));
    }

    @GetMapping
    public BaseResponse<List<ProductResponseDto>> getAll(
            @RequestParam(name = "huruf", required = false) String letter) {
        return BaseResponse.ok("Products retrieved successfully", productService.getProducts(letter));
    }

    @PutMapping("/{id}")
    public BaseResponse<ProductResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto request) {
        return BaseResponse.ok("Product updated successfully", productService.updateProduct(id, request));
    }
    @DeleteMapping("/{id}")
    public BaseResponse<String> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return BaseResponse.ok("Product deleted successfully", "Product with ID " + id + " has been removed");
    }
}
