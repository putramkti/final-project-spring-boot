package com.bootcamp19.scheduler_service.rest;

import com.bootcamp19.scheduler_service.dto.request.ProductImportRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    @PostMapping("/product/internal/import")
    Object sendBatchForImport(@RequestBody List<ProductImportRequestDto> batch);
}
