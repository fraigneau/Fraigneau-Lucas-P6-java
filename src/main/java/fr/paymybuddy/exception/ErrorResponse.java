package fr.paymybuddy.exception;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ErrorResponse {
    private int status;
    private int errorCode;
    private String code;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, int errorCode, String code, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String code, String message) {
        this.status = 400;
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
