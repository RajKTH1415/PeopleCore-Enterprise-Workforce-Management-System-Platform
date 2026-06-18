package com.peoplecore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WorkflowTemplateRequest {

    @NotBlank(message = "Template name is required")
    @Size(
            min = 3,
            max = 100,
            message = "Template name must be between 3 and 100 characters"
    )
    private String templateName;

    @Size(
            max = 500,
            message = "Description cannot exceed 500 characters"
    )
    private String description;
}
