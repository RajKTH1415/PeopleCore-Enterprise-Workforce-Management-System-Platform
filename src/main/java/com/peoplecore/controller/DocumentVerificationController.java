package com.peoplecore.controller;
import com.peoplecore.dto.response.DocumentResponse;
import com.peoplecore.service.DocumentVerificationService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/documents/verification")
@RequiredArgsConstructor
public class DocumentVerificationController {

    private final DocumentVerificationService documentVerificationService;

    @PutMapping("/{documentId}/verify")
    public ResponseEntity<ApiResponse<DocumentResponse>> verifyDocument(@PathVariable @NotBlank(message = "Document ID is required")String documentId, HttpServletRequest httpServletRequest) {
        DocumentResponse response = documentVerificationService.verifyDocument(documentId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Document verified successfully", httpServletRequest.getRequestURI(), response));
    }

    @PutMapping("/{documentId}/reject")
    public ResponseEntity<ApiResponse<DocumentResponse>> rejectDocument(@PathVariable String documentId, @RequestParam String reason, HttpServletRequest httpServletRequest) {
        DocumentResponse response = documentVerificationService.rejectDocument(documentId, reason, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Document rejected successfully", httpServletRequest.getRequestURI(), response));
    }
}