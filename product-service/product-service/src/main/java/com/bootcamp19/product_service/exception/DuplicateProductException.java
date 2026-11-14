package com.bootcamp19.product_service.exception;

public class DuplicateProductException extends RuntimeException {
    public DuplicateProductException(String name) {
        super("Product with name '" + name + "' already exists");
    }
}
