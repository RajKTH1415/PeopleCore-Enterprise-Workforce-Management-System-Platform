package com.peoplecore.controller;

import com.peoplecore.dto.response.DocumentResponse;
import com.peoplecore.dto.response.DocumentVersionResponse;
import com.peoplecore.dto.response.DownloadDocumentResponse;
import com.peoplecore.service.EmployeesDocumentsService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ResponseEntity<Resource> downloadDocument(@PathVariable String documentId, HttpServletRequest request) {
        DownloadDocumentResponse response = employeesDocumentsService.downloadDocument(documentId, request);

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

    @PostMapping("/{documentId}/new-version")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadNewVersion(
            @PathVariable String documentId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        DocumentResponse response =
                employeesDocumentsService.uploadNewVersion(
                        documentId,
                        file,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        200,
                        "New document version uploaded successfully",
                        request.getRequestURI(),
                        response
                )
        );
    }

    @GetMapping("/{documentId}/versions")
    public ResponseEntity<ApiResponse<List<DocumentVersionResponse>>> getVersions(@PathVariable String documentId, HttpServletRequest httpServletRequest) {
        List<DocumentVersionResponse> documentVersionResponse = employeesDocumentsService.getVersions(documentId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Document versions fetched successfully", httpServletRequest.getRequestURI(), documentVersionResponse));
    }

    @GetMapping("/{documentId}/versions/{version}")
    public ResponseEntity<ApiResponse<DocumentVersionResponse>> getVersion(
            @PathVariable String documentId,
            @PathVariable Integer version,
            HttpServletRequest httpServletRequest) {

        DocumentVersionResponse documentVersionResponse =
                employeesDocumentsService.getVersion(documentId, version);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Document version fetched successfully",
                        httpServletRequest.getRequestURI(),
                        documentVersionResponse));
    }
}
