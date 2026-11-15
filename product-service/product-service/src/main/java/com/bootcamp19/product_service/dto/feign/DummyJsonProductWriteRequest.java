package com.bootcamp19.product_service.dto.feign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DummyJsonProductWriteRequest {
    private String title;
    private BigDecimal price;
    private String category;
    private String brand;
}
