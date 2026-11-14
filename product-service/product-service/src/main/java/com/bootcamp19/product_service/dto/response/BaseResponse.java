package com.bootcamp19.product_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    private String status;
    private String message;
    private T data;

    public static <T> BaseResponse<T> ok(T data) {
        return new BaseResponse<>("success", "Operation successful", data);
    }

    public static <T> BaseResponse<T> ok(String message, T data) {
        return new BaseResponse<>("success", message, data);
    }

    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>("failed", message, null);
    }
}
