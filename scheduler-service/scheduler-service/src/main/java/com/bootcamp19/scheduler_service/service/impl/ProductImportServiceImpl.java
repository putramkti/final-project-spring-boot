package com.bootcamp19.scheduler_service.service.impl;

import com.bootcamp19.scheduler_service.dto.feign.DummyJsonProduct;
import com.bootcamp19.scheduler_service.dto.feign.DummyJsonProductListResponse;
import com.bootcamp19.scheduler_service.dto.request.ProductImportRequestDto;
import com.bootcamp19.scheduler_service.rest.DummyJsonClient;
import com.bootcamp19.scheduler_service.rest.ProductServiceClient;
import com.bootcamp19.scheduler_service.service.ProductImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductImportServiceImpl implements ProductImportService {
    private final DummyJsonClient dummyJsonClient;
    private final ProductServiceClient productServiceClient;

    @Value("${scheduler.batch-size}")
    private int batchSize;

    private int currentOffset = 0;

    @Override
    public synchronized void executeImport(String triggerSource) {
        log.info("[Scheduler] Starting product import sequence. Triggered by: {}", triggerSource);

        DummyJsonProductListResponse response;

        try {
            response = dummyJsonClient.getSmartphones();
        } catch (Exception ex) {
            log.error("[Scheduler] Unable to fetch products from DummyJSON: {}", ex.getMessage());
            return;
        }

        if (response == null || response.getProducts() == null || response.getProducts().isEmpty()) {
            log.warn("[Scheduler] No smartphone records received from DummyJSON");
            return;
        }

        List<DummyJsonProduct> allProducts = response.getProducts();
        int totalProducts = allProducts.size();

        int startIndex = currentOffset % totalProducts;
        int endIndex = Math.min(startIndex + batchSize, totalProducts);

        List<DummyJsonProduct> batchSlice = allProducts.subList(startIndex, endIndex);

        List<ProductImportRequestDto> dtoList = toDtoList(batchSlice);

        try {
            productServiceClient.sendBatchForImport(dtoList);
            log.info("[Scheduler] Dispatched {} items to product-service successfully", dtoList.size());

            currentOffset = (endIndex >= totalProducts) ? 0 : endIndex;
        } catch (Exception ex) {
            log.error("[Scheduler] Failed to dispatch batch to product-service: {}", ex.getMessage());
        }

    }

    private List<ProductImportRequestDto> toDtoList(List<DummyJsonProduct> products) {
        List<ProductImportRequestDto> dtoList = new ArrayList<>();
        for (DummyJsonProduct product : products) {
            dtoList.add(ProductImportRequestDto.builder()
                    .name(product.getTitle())
                    .price(product.getPrice())
                    .category(product.getCategory())
                    .brand(product.getBrand() != null ? product.getBrand() : "No brand")
                    .rating(product.getRating())
                    .build());
        }

        return dtoList;
    }
}
