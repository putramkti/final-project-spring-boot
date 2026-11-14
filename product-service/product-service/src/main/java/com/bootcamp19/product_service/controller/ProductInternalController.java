package com.bootcamp19.product_service.controller;

import com.bootcamp19.product_service.dto.request.ProductBatchImportDto;
import com.bootcamp19.product_service.dto.response.BaseResponse;
import com.bootcamp19.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductInternalController {

    private final ProductService productService;

    @PostMapping("/import")
    public BaseResponse<String> receiveImportBatch(@RequestBody List<ProductBatchImportDto> batch) {
        log.info("Received internal batch import containing {} items", batch.size());
        int savedCount = productService.importProducts(batch);
        return BaseResponse.ok("Batch import processed", "Successfully imported " + savedCount + " new products");
    }
}