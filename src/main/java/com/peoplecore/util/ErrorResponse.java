package com.peoplecore.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status",
        "error",
        "message",
        "path",
        "validationErrors",
        "timestamp"
})
public class ErrorResponse {

    private int status;

    private String error;

    private String message;

    private String path;

    private Map<String, String> validationErrors;

    private LocalDateTime timestamp;
}