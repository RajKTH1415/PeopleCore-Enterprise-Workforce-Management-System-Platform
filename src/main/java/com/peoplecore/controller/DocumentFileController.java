package com.peoplecore.controller;

import com.peoplecore.dto.response.DocumentResponse;
import com.peoplecore.dto.response.DownloadDocumentResponse;
import com.peoplecore.service.EmployeesDocumentsService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents/files")
public class DocumentFileController {


    private final EmployeesDocumentsService employeesDocumentsService;

    public DocumentFileController(EmployeesDocumentsService employeesDocumentsService) {
        this.employeesDocumentsService = employeesDocumentsService;
    }

    @PutMapping("/{employeeId}/documents/{documentId}/replace")
    public ResponseEntity<ApiResponse<DocumentResponse>> replaceDocument(
            @PathVariable Long employeeId,
            @PathVariable String documentId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        DocumentResponse response = employeesDocumentsService
                .replaceDocument(employeeId, documentId, file, request);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Document replaced successfully",
                        request.getRequestURI(), response)
        );
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable String documentId,
            HttpServletRequest request) {

        DownloadDocumentResponse response =
                employeesDocumentsService.downloadDocument(documentId, request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + response.getFileName() + "\"")
                .body(response.getResource());
    }

    @GetMapping("/{documentId}/preview")
    public ResponseEntity<Resource> previewDocument(
            @PathVariable String documentId,
            HttpServletRequest request) {

        DownloadDocumentResponse response =
                employeesDocumentsService.previewDocument(documentId, request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + response.getFileName() + "\"")
                .body(response.getResource());
    }
}
