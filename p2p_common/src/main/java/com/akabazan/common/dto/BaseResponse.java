package com.akabazan.common.dto;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class BaseResponse<T> {

    private String status;
    private int code;
    private String message;
    private T result;
    private PaginationMeta meta;
    private PaginationLinks links;
    private List<ErrorDetail> errors;

    public BaseResponse() {}

    public BaseResponse(String status, int code, String message, T result) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.result = result;
    }

    // --- Static factories ---
    public static <T> BaseResponse<T> success(T result) {
        return new BaseResponse<>("success", 200, "Success", result);
    }

    public static <T> BaseResponse<T> success(String message, T result) {
        return new BaseResponse<>("success", 200, message, result);
    }

    public static <T> BaseResponse<T> error(int statusCode, String message) {
        return error(statusCode, message, Collections.emptyList());
    }

    public static <T> BaseResponse<T> error(int statusCode, String message, List<ErrorDetail> errors) {
        BaseResponse<T> response = new BaseResponse<>("error", statusCode, message, null);
        response.setErrors(errors);
        return response;
    }

    // --- Pagination helper ---
    public static <T, R> BaseResponse<List<R>> fromPage(Page<T> page, Function<T, R> mapper) {
        List<R> data = page.getContent().stream().map(mapper).collect(Collectors.toList());
        BaseResponse<List<R>> response = success("OK", data);

        response.setMeta(new PaginationMeta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        ));

        String self = buildPageLink(page.getNumber(), page.getSize());
        String next = page.hasNext() ? buildPageLink(page.getNumber() + 1, page.getSize()) : null;
        String prev = page.hasPrevious() ? buildPageLink(page.getNumber() - 1, page.getSize()) : null;

        response.setLinks(new PaginationLinks(self, next, prev));
        return response;
    }

    private static String buildPageLink(int page, int size) {
        
                 return ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .toUriString();
    }

    // --- Getters & setters ---
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getResult() { return result; }
    public void setResult(T result) { this.result = result; }
    public PaginationMeta getMeta() { return meta; }
    public void setMeta(PaginationMeta meta) { this.meta = meta; }
    public PaginationLinks getLinks() { return links; }
    public void setLinks(PaginationLinks links) { this.links = links; }
    public List<ErrorDetail> getErrors() { return errors; }
    public void setErrors(List<ErrorDetail> errors) { this.errors = errors; }

    // --- Nested classes ---
    public static class PaginationMeta {
        private final int page;
        private final int size;
        private final long totalElements;
        private final int totalPages;
        private final boolean hasNext;
        private final boolean hasPrevious;

        public PaginationMeta(int page, int size, long totalElements, int totalPages, boolean hasNext, boolean hasPrevious) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
        }

        public int getPage() { return page; }
        public int getSize() { return size; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public boolean isHasNext() { return hasNext; }
        public boolean isHasPrevious() { return hasPrevious; }
    }

    public static class PaginationLinks {
        private final String self;
        private final String next;
        private final String prev;

        public PaginationLinks(String self, String next, String prev) {
            this.self = self;
            this.next = next;
            this.prev = prev;
        }

        public String getSelf() { return self; }
        public String getNext() { return next; }
        public String getPrev() { return prev; }
    }

    public static class ErrorDetail {
        private final String field;
        private final String message;

        public ErrorDetail(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() { return field; }
        public String getMessage() { return message; }
    }
}
