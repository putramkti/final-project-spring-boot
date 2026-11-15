package com.bootcamp19.scheduler_service.rest;

import com.bootcamp19.scheduler_service.dto.feign.DummyJsonProductListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "DummyJsonClient", url = "${dummyjson.url}")
public interface DummyJsonClient {
    @GetMapping("/products/category/smartphones")
    DummyJsonProductListResponse getSmartphones();
}
