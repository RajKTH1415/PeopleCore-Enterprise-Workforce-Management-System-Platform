package com.peoplecore.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "status", "message", "path", "data", "timestamp" })
public class ApiResponse<T> {

    private int status;
    private String message;
    private String path;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private T data;
    private LocalDateTime timestamp;

  public static <T> ApiResponse<T> success(int status , String message, String path , T data) {
    return ApiResponse.<T>builder()
            .status(status)
            .message(message)
            .path(path)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
}

    public static <T> ApiResponse<T> error(HttpStatus status, String message, String path, T data) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .message(message)
                .path(path)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

}
