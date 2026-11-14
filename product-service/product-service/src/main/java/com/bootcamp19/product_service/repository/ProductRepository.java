package com.bootcamp19.product_service.repository;

import com.bootcamp19.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
