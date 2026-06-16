package com.peoplecore.util;


import com.peoplecore.exception.FileValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public class DocumentValidator {

    private static final long MAX_SIZE = 5 * 1024 * 1024;

    private static final List<String> ALLOWED_TYPES =
            List.of(
                    "application/pdf",
                    "image/jpeg",
                    "image/jpg",
                    "image/png"
            );

    public static void validate(
            MultipartFile file,
            String documentType,
            String category,
            String title,
            LocalDate issueDate,
            LocalDate expiryDate
    ) {

        if (file == null || file.isEmpty()) {
            throw new FileValidationException("File is required");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new FileValidationException(
                    "File size must not exceed 5 MB");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new FileValidationException(
                    "Only PDF, JPG, JPEG and PNG files are allowed");
        }

        if (documentType == null || documentType.isBlank()) {
            throw new FileValidationException(
                    "Document type is required");
        }

        if (category == null || category.isBlank()) {
            throw new FileValidationException(
                    "Category is required");
        }

        if (title == null || title.isBlank()) {
            throw new FileValidationException(
                    "Title is required");
        }

        if (issueDate != null &&
                expiryDate != null &&
                expiryDate.isBefore(issueDate)) {

            throw new FileValidationException(
                    "Expiry date cannot be before issue date");
        }
    }
}
