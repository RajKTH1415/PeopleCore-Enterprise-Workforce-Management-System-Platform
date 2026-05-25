package com.peoplecore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CityRequest {

    @NotNull(message = "State id is required")
    private Long stateId;

    @NotBlank(message = "City code is required")
    private String code;

    @NotBlank(message = "City name is required")
    private String name;

    @NotBlank(message = "Pin code is required")
    private String pinCode;
}
