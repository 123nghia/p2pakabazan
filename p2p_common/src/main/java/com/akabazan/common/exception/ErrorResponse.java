package com.akabazan.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final String status;
    private final int code;
    private final String message;
    private final String errorCode;
    private final List<FieldError> errors;
    private final String path;
    private final LocalDateTime timestamp;

    public ErrorResponse(int code, String message, String errorCode, List<FieldError> errors, String path) {
        this.status = "error";
        this.code = code;
        this.message = message;
        this.errorCode = errorCode;
        this.errors = errors;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int code, String message, String errorCode, String path) {
        this(code, message, errorCode, Collections.emptyList(), path);
    }

    public ErrorResponse(int code, String message, List<FieldError> errors, String path) {
        this(code, message, null, errors, path);
    }

    public ErrorResponse(int code, String message, String path) {
        this(code, message, null, Collections.emptyList(), path);
    }

    public String getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public static class FieldError {
        private final String field;
        private final String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }
}
