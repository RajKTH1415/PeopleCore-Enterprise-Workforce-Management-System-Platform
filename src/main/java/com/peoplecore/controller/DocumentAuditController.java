package com.peoplecore.controller;

import com.peoplecore.dto.response.DocumentAccessLogResponse;
import com.peoplecore.dto.response.DocumentVersionResponse;
import com.peoplecore.dto.response.EmployeeDocumentAuditResponse;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.service.DocumentAuditService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents/audit")
public class DocumentAuditController {


    private final DocumentAuditService documentAuditService;

    public DocumentAuditController(DocumentAuditService documentAuditService) {
        this.documentAuditService = documentAuditService;
    }


    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeDocumentAuditResponse>>> getAuditLogs(

            @PathVariable Long documentId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "performedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction,

            HttpServletRequest httpServletRequest) {

        PageResponse<EmployeeDocumentAuditResponse> response =
                documentAuditService.getAuditLogs(
                        documentId,
                        page,
                        size,
                        sortBy,
                        direction,
                        httpServletRequest
                );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Document audit logs fetched successfully",
                        httpServletRequest.getRequestURI(),
                        response
                ));
    }

    @GetMapping("/{documentId}/access-log")
    public ResponseEntity<ApiResponse<PageResponse<DocumentAccessLogResponse>>> getAccessLogs(

            @PathVariable String documentId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "accessedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction,

            HttpServletRequest httpServletRequest) {

        PageResponse<DocumentAccessLogResponse> response =
                documentAuditService.getAccessLogs(
                        documentId,
                        page,
                        size,
                        sortBy,
                        direction,
                        httpServletRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Document access logs fetched successfully",
                        httpServletRequest.getRequestURI(),
                        response));
    }
    @GetMapping("/{documentId}/versions")
    public ResponseEntity<ApiResponse<List<DocumentVersionResponse>>> getDocumentVersions(@PathVariable String documentId, HttpServletRequest httpServletRequest) {
        List<DocumentVersionResponse> documentVersionResponse = documentAuditService.getDocumentVersions(documentId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Document version history fetched successfully", httpServletRequest.getRequestURI(), documentVersionResponse));
    }
}
