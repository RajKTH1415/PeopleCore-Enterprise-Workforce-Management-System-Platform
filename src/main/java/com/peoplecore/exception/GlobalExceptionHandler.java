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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Validation Failed")
                .message("Request validation error")
                .validationErrors(errors)
                .build();

        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation Failed")
                .path(request.getRequestURI())
                .data(errorResponse)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    // ================= BAD REQUEST (ALL USER OPS) =================
    @ExceptionHandler({
            BadRequestException.class,
            UserRestoreException.class,
            UserDeactivationException.class,
            UserActivationException.class,
            UserDeletionException.class,
            UserSoftDeleteException.class,
            UserUpdateException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // ================= DUPLICATE =================
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDuplicate(
            DuplicateResourceException ex,
            HttpServletRequest request
    ) {

        ErrorResponse error = ErrorResponse.builder()
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>builder()
                .status(HttpStatus.CONFLICT.value())
                .message("Resource already exists")
                .path(request.getRequestURI())
                .data(error)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // ================= NOT FOUND =================
    @ExceptionHandler({
            ResourceNotFoundException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // ================= UNAUTHORIZED =================
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request
    ) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // ================= FORBIDDEN =================
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenException ex,
            HttpServletRequest request
    ) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // ================= DATABASE ERROR =================
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDB(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message("Database integrity violation")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // ================= CONSTRAINT VALIDATION =================
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {

        Map<String, String> validationErrors = new HashMap<>();

        ex.getConstraintViolations().forEach(v ->
                validationErrors.put(v.getPropertyPath().toString(), v.getMessage())
        );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation Failed")
                .path(request.getRequestURI())
                .validationErrors(validationErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // ================= GLOBAL FALLBACK =================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(
            Exception ex,
            HttpServletRequest request
    ) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Something went wrong")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserBulkDeletionException.class)
    public ResponseEntity<ErrorResponse> handleUserBulkDeletionException(
            UserBulkDeletionException ex,
            HttpServletRequest request
    ) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoData(
            NoDataFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(
                        HttpStatus.NOT_FOUND,
                        ex.getMessage(),
                        request.getRequestURI(),
                        null
                ));
    }
}