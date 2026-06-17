package com.peoplecore.exception;

import com.peoplecore.util.ApiResponse;
import com.peoplecore.util.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ================= COMMON ERROR BUILDER =================
    private ResponseEntity<ApiResponse<ErrorResponse>> buildError(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErrors
    ) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(status.getReasonPhrase())
                .message(message)
                .validationErrors(validationErrors)
                .build();

        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>builder()
                .status(status.value())
                .message(message)
                .path(request.getRequestURI())
                .data(errorResponse)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    // ================= DTO VALIDATION =================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                request,
                errors
        );
    }

    // ================= CONSTRAINT VALIDATION =================
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleConstraint(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {

        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(v ->
                errors.put(v.getPropertyPath().toString(), v.getMessage())
        );

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                request,
                errors
        );
    }

    // ================= BAD REQUEST =================
    @ExceptionHandler({
            BadRequestException.class,
            UserRestoreException.class,
            UserDeactivationException.class,
            UserActivationException.class,
            UserDeletionException.class,
            UserSoftDeleteException.class,
            UserUpdateException.class,
            InvalidRequestException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBadRequest(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request,
                null
        );
    }

    // ================= NOT FOUND =================
    @ExceptionHandler({
            ResourceNotFoundException.class,
            UserNotFoundException.class,
            DocumentNotFoundException.class
    })
    public ResponseEntity<ApiResponse<ErrorResponse>> handleNotFound(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        return buildError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request,
                null
        );
    }

    // ================= CONFLICT (DUPLICATE) =================
    @ExceptionHandler({
            DuplicateResourceException.class,
            DocumentAlreadyVerifiedException.class,
            DocumentAlreadyRejectedException.class,
            CountryAlreadyExistsException.class
    })
    public ResponseEntity<ApiResponse<ErrorResponse>> handleConflict(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        return buildError(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request,
                null
        );
    }

    // ================= UNAUTHORIZED =================
    @ExceptionHandler({
            UnauthorizedException.class
    })
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request
    ) {

        return buildError(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request,
                null
        );
    }

    // ================= FORBIDDEN =================
    @ExceptionHandler({
            ForbiddenException.class,
            AccessDeniedException.class,
            UnauthorizedResourceAccessException.class
    })
    public ResponseEntity<ApiResponse<ErrorResponse>> handleForbidden(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        return buildError(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request,
                null
        );
    }

    // ================= DATABASE =================
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDB(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {

        return buildError(
                HttpStatus.CONFLICT,
                "Database integrity violation",
                request,
                null
        );
    }

    // ================= FILE / UPLOAD =================
    @ExceptionHandler({
            MaxUploadSizeExceededException.class,
            FileValidationException.class,
            DocumentUploadException.class,
            InvalidDocumentException.class
    })
    public ResponseEntity<ApiResponse<ErrorResponse>> handleFileErrors(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        HttpStatus status = (ex instanceof DocumentUploadException)
                ? HttpStatus.INTERNAL_SERVER_ERROR
                : HttpStatus.BAD_REQUEST;

        return buildError(
                status,
                ex.getMessage(),
                request,
                null
        );
    }

    // ================= BULK OPERATIONS =================
    @ExceptionHandler({
            UserBulkDeletionException.class,
            DocumentBulkDeletionException.class
    })
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBulk(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request,
                null
        );
    }

    // ================= GLOBAL FALLBACK =================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleGlobal(
            Exception ex,
            HttpServletRequest request
    ) {

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong",
                request,
                null
        );
    }
}