package com.akabazan.common.dto;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public class ResponseFactory {

    public static <T> ResponseEntity<BaseResponse<T>> ok(T data) {
        return ResponseEntity.ok(BaseResponse.success("OK", data));
    }

    public static <T> ResponseEntity<BaseResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(BaseResponse.success(message, data));
    }

    public static <T> ResponseEntity<BaseResponse<T>> error(int code, String message) {
        return ResponseEntity.status(code).body(BaseResponse.error(code, message));
    }
    public static <T, R> ResponseEntity<BaseResponse<List<R>>> paged(Page<T> page, Function<T, R> mapper) {
        return ResponseEntity.ok(BaseResponse.fromPage(page, mapper));
    }
}
