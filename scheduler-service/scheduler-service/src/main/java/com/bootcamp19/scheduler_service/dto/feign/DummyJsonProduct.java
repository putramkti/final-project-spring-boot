package com.bootcamp19.scheduler_service.dto.feign;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DummyJsonProduct {
    private Long id;
    private String title;
    private BigDecimal price;
    private String category;
    private String brand;
    private Double rating;
}