package com.peoplecore.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateDocumentRequest {

    @Size(max = 255,
            message = "Title cannot exceed 255 characters")
    private String title;

    @Size(max = 1000,
            message = "Description cannot exceed 1000 characters")
    private String description;

    @Size(max = 100,
            message = "Document number cannot exceed 100 characters")
    private String documentNumber;

    private LocalDate issueDate;

    private LocalDate expiryDate;

    private Boolean isPrimary;
}