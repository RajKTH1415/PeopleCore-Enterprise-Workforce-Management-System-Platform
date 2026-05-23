package com.peoplecore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StateRequest {

    @NotNull(message = "Country id is required")
    private Long countryId;

    @NotBlank(message = "State code is required")
    @Size(max = 10, message = "State code cannot exceed 10 characters")
    private String code;

    @NotBlank(message = "State name is required")
    @Size(max = 100, message = "State name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Capital is required")
    @Size(max = 100, message = "Capital cannot exceed 100 characters")
    private String capital;
}