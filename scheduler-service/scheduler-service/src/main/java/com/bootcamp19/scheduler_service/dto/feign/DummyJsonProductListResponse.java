package com.bootcamp19.scheduler_service.dto.feign;

import lombok.Data;

import java.util.List;

@Data
public class DummyJsonProductListResponse {
    private List<DummyJsonProduct> products;
    private Integer total;
    private Integer skip;
    private Integer limit;
}