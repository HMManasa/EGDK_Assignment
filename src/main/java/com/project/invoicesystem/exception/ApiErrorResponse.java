package com.project.invoicesystem.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private String details;

    public ApiErrorResponse(String message, String details) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.details = details;
    }

}
