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
        log.info("[Scheduler] Starting import. Trigger: {}, Offset: {}, Limit: {}", triggerSource, currentOffset, batchSize);

        DummyJsonProductListResponse response;
        try {
            // Ambil langsung 5 produk dari DummyJSON menggunakan limit & skip
            response = dummyJsonClient.getSmartphones(batchSize, currentOffset);
        } catch (Exception ex) {
            log.error("[Scheduler] Unable to fetch products from DummyJSON: {}", ex.getMessage());
            return;
        }

        if (response == null || response.getProducts() == null || response.getProducts().isEmpty()) {
            log.warn("[Scheduler] No records or end of category reached. Resetting offset to 0.");
            currentOffset = 0;
            return;
        }

        List<ProductImportRequestDto> dtoList = toDtoList(response.getProducts());

        try {
            productServiceClient.sendBatchForImport(dtoList);
            log.info("[Scheduler] Dispatched {} items to product-service successfully", dtoList.size());

            // Update offset untuk putaran berikutnya
            currentOffset += dtoList.size();
            if (response.getTotal() != null && currentOffset >= response.getTotal()) {
                log.info("[Scheduler] Reached total products ({}), resetting offset for next cycle", response.getTotal());
                currentOffset = 0;
            }
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
