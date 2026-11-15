package com.bootcamp19.scheduler_service.rest;

import com.bootcamp19.scheduler_service.dto.feign.DummyJsonProductListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "DummyJsonClient", url = "${dummyjson.url}")
public interface DummyJsonClient {
    @GetMapping("/products/category/smartphones")
    DummyJsonProductListResponse getSmartphones(
            @RequestParam("limit") int limit,
            @RequestParam("skip") int skip
    );
}
