package com.peoplecore.controller;
import com.peoplecore.dto.request.UpdateDocumentRequest;
import com.peoplecore.dto.response.*;
import com.peoplecore.service.EmployeesDocumentsService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees/documents")
@Validated
public class DocumentController {


    private final EmployeesDocumentsService employeesDocumentsService;

    public DocumentController(EmployeesDocumentsService employeesDocumentsService) {
        this.employeesDocumentsService = employeesDocumentsService;
    }


    @PostMapping(value = "/{employeeId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @PathVariable   @Positive(message = "Employee id must be positive") Long employeeId,
            @RequestParam("file")  MultipartFile file,
            @RequestParam    @NotBlank(message = "Document type is required") String documentType,
            @RequestParam   @NotBlank(message = "Category is required") String category,
            @RequestParam  @NotBlank(message = "Title is required") String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String documentNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
            @RequestParam(required = false, defaultValue = "false") Boolean isPrimary,
            @RequestParam(required = false) List<String> tags,
            HttpServletRequest httpServletRequest) {

        DocumentResponse response = employeesDocumentsService.uploadDocument(
                employeeId, file, documentType, category, title,
                description, documentNumber, issueDate, expiryDate,
                isPrimary, tags, httpServletRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Document uploaded successfully", httpServletRequest.getRequestURI(), response));
    }

    /* it's not use only for delete all data from documents table */
    @DeleteMapping("/all")
    public ResponseEntity<ApiResponse<String>> deleteAllDocuments(
            HttpServletRequest request) {

        employeesDocumentsService.deleteAllDocumentsSystem();

        return ResponseEntity.ok(
                ApiResponse.success(200, "All documents deleted successfully",
                        request.getRequestURI(), null));
    }

    @GetMapping("/{employeeId}/documents/{documentId}")
    public ResponseEntity<ApiResponse<DocumentDetailsResponse>> getDocumentById(
            @PathVariable   @Positive(message = "Employee ID must be greater than zero") Long employeeId,
            @PathVariable @NotBlank(message = "Document ID is required") String documentId,
            HttpServletRequest httpServletRequest) {

        DocumentDetailsResponse response =
                employeesDocumentsService.getDocumentById(employeeId, documentId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Document fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{employeeId}/documents")
    public ResponseEntity<ApiResponse<PageResponse<DocumentResponse>>> getAllDocuments(
            @PathVariable  @Positive(message = "Employee ID must be greater than zero") Long employeeId,

            @RequestParam(required = false) String documentType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String verificationStatus,

            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) Boolean isPrimary,

            @RequestParam(required = false) LocalDate expiryBefore,
            @RequestParam(required = false) LocalDate expiryAfter,
            @RequestParam(required = false) Boolean expired,

            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags,

            @Min(value = 0, message = "Page cannot be negative")
            @RequestParam(defaultValue = "0")
            int page,
            @Min(value = 1, message = "Size must be greater than zero")
            @Max(value = 100, message = "Maximum page size is 100")
            @RequestParam(defaultValue = "10")
            int size,
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir, HttpServletRequest httpServletRequest) {

        PageResponse<DocumentResponse> response =
                employeesDocumentsService.getAllDocuments(
                        employeeId,
                        documentType,
                        category,
                        verificationStatus,
                        isDeleted,
                        isPrimary,
                        expiryBefore,
                        expiryAfter,
                        expired,
                        search,
                        tags,
                        page,
                        size,
                        sortBy,
                        sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Documents fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DocumentResponse>>> getDocuments(
            @RequestParam(required = false)   @Positive(message = "Employee ID must be greater than zero") Long employeeId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String verificationStatus,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) Boolean isPrimary,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,

            HttpServletRequest httpServletRequest) {

        PageResponse<DocumentResponse> response =
                employeesDocumentsService.getDocuments(
                        employeeId,
                        type,
                        status,
                        verificationStatus,
                        category,
                        isDeleted,
                        isPrimary,
                        search,
                        tags,
                        page,
                        size,
                        sortBy,
                        sortDir);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Documents fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDocumentById(
            @PathVariable String documentId,
            HttpServletRequest httpServletRequest) {

        DocumentResponse response = employeesDocumentsService.getDocumentById(documentId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Document fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @PutMapping("/{employeeId}/documents/{documentId}")
    public ResponseEntity<ApiResponse<DocumentResponse>> updateDocumentMetadata(
            @PathVariable Long employeeId,
            @PathVariable String documentId,
            @RequestBody UpdateDocumentRequest request,
            HttpServletRequest httpServletRequest) {

        DocumentResponse response = employeesDocumentsService.updateDocumentMetadata(
                employeeId,
                documentId,
                request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Document metadata updated successfully", httpServletRequest.getRequestURI(), response));
    }

    @DeleteMapping("/{employeeId}/documents/{documentId}")
    public ResponseEntity<ApiResponse<DeleteDocumentResponse>> softDeleteDocument(
            @PathVariable Long employeeId,
            @PathVariable String documentId,
            HttpServletRequest httpServletRequest) {

        DeleteDocumentResponse deleteDocumentResponse = employeesDocumentsService.deleteDocument(employeeId, documentId, httpServletRequest);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Document deleted successfully", httpServletRequest.getRequestURI(), deleteDocumentResponse));
    }

    @PutMapping("/{employeeId}/documents/{documentId}/restore")
    public ResponseEntity<ApiResponse<RestoreDocumentResponse>> restoreDocument(
            @PathVariable Long employeeId,
            @PathVariable String documentId,
            HttpServletRequest request) {

        RestoreDocumentResponse response =
                employeesDocumentsService.restoreDocument(employeeId, documentId, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Document restored successfully", request.getRequestURI(), response));
    }

    @PutMapping("/{employeeId}/documents/{documentId}/versions/{version}/restore")
    public ResponseEntity<ApiResponse<DocumentResponse>> restoreVersion(
            @PathVariable Long employeeId,
            @PathVariable String documentId,
            @PathVariable Integer version,
            HttpServletRequest request) {

        DocumentResponse response = employeesDocumentsService
                .restoreVersion(employeeId, documentId, version, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Version restored successfully", request.getRequestURI(), response));
    }
}
