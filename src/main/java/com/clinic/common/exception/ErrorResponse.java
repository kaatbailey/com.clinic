package com.clinic.common.exception;

import lombok.Value;
import java.time.LocalDateTime;

@Value
public class ErrorResponse {
    int status;
    String error;
    String message;
    LocalDateTime timestamp;
}