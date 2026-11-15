package com.bootcamp19.scheduler_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImportRequestDto {
    private String name;
    private BigDecimal price;
    private String category;
    private String brand;
    private Double rating;
}
