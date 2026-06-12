package com.peoplecore.service;

import com.peoplecore.dto.request.UpdateDocumentRequest;
import com.peoplecore.dto.response.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface EmployeesDocumentsService {

    /*just for testing*/
    void deleteAllDocumentsSystem();

    DocumentDetailsResponse getDocumentById(Long employeeId, String documentId);


    DocumentResponse uploadDocument(
            Long employeeId,
            MultipartFile file,
            String documentType,
            String category,
            String title,
            String description,
            String documentNumber,
            LocalDate issueDate,
            LocalDate expiryDate,
            Boolean isPrimary,
            List<String> tags,
            HttpServletRequest request
    );
    PageResponse<DocumentResponse> getAllDocuments(
            Long employeeId,
            String documentType,
            String category,
            String verificationStatus,
            Boolean isDeleted,
            Boolean isPrimary,
            LocalDate expiryBefore,
            LocalDate expiryAfter,
            Boolean expired,
            String search,
            List<String> tags,
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    PageResponse<DocumentResponse> getDocuments(
            Long employeeId,
            String type,
            String status,
            String verificationStatus,
            String category,
            Boolean isDeleted,
            Boolean isPrimary,
            String search,
            List<String> tags,
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    DocumentResponse getDocumentById(String documentId);

    DocumentResponse updateDocumentMetadata(
            Long employeeId,
            String documentId,
            UpdateDocumentRequest request
    );

    DeleteDocumentResponse deleteDocument(Long employeeId, String documentId, HttpServletRequest request);

    RestoreDocumentResponse restoreDocument(
            Long employeeId,
            String documentId,
            HttpServletRequest request);

    DocumentResponse restoreVersion(Long employeeId,
                                    String documentId,
                                    Integer version,
                                    HttpServletRequest request);


    DocumentResponse replaceDocument(Long employeeId,
                                     String documentId,
                                     MultipartFile file,
                                     HttpServletRequest request);

    DownloadDocumentResponse downloadDocument(String documentId,
                                              HttpServletRequest request);


    DownloadDocumentResponse previewDocument(
            String documentId,
            HttpServletRequest request);

    DocumentResponse uploadNewVersion(
            String documentId,
            MultipartFile file,
            HttpServletRequest request);

    List<DocumentVersionResponse> getVersions(
            String documentId);

    DocumentVersionResponse getVersion(
            String documentId,
            Integer version);
}
