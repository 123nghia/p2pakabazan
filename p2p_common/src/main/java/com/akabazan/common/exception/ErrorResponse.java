package com.akabazan.common.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;          // HTTP status code
    private String error;        // Error type (Bad Request, Internal Error...)
    private String code;         // Custom error code (from ErrorCode enum)
    private String message;      // Human-readable message
    private String path;         // API path
    private LocalDateTime timestamp;
    // Constructor với code + message
    public ErrorResponse(int status, String error, String code, String message, String path) {
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
    // Constructor đơn giản khi chỉ có message
    public ErrorResponse(int status, String error, String message, String path) {
        this(status, error, null, message, path);
    }

    // Getters
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
