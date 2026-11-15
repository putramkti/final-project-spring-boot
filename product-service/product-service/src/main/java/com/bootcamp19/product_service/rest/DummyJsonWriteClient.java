package com.bootcamp19.product_service.rest;

import com.bootcamp19.product_service.dto.feign.DummyJsonProductWriteRequest;
import com.bootcamp19.product_service.dto.feign.DummyJsonProductWriteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name ="dummyJsonWriteClient", url = "${dummyjson.url}")
public interface DummyJsonWriteClient {
    @PostMapping("/products/add")
    DummyJsonProductWriteResponse addProduct(@RequestBody DummyJsonProductWriteRequest request);

    @PutMapping("/products/{id}")
    DummyJsonProductWriteResponse updateProduct(@PathVariable("id") Long id, @RequestBody DummyJsonProductWriteRequest request);

    @DeleteMapping("/products/{id}")
    DummyJsonProductWriteResponse deleteProduct(@PathVariable("id") Long id);
}
