package com.peoplecore.service.Impl;

import com.peoplecore.dto.response.DocumentResponse;
import com.peoplecore.enums.ActionType;
import com.peoplecore.exception.DocumentAlreadyRejectedException;
import com.peoplecore.exception.DocumentAlreadyVerifiedException;
import com.peoplecore.exception.DocumentNotFoundException;
import com.peoplecore.exception.InvalidDocumentStatusException;
import com.peoplecore.module.EmployeeDocument;
import com.peoplecore.module.EmployeeDocumentAudit;
import com.peoplecore.repository.DocumentAuditRepository;
import com.peoplecore.repository.EmployeeDocumentRepository;
import com.peoplecore.service.DocumentVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class DocumentVerificationServiceImpl implements DocumentVerificationService {

    private final EmployeeDocumentRepository employeeDocumentRepository;

    private final DocumentAuditRepository documentAuditRepository;

    public DocumentVerificationServiceImpl(EmployeeDocumentRepository employeeDocumentRepository, DocumentAuditRepository documentAuditRepository) {
        this.employeeDocumentRepository = employeeDocumentRepository;
        this.documentAuditRepository = documentAuditRepository;
    }

    @Override
    @Transactional
    public DocumentResponse verifyDocument(String documentId,
                                           HttpServletRequest request) {

        if (documentId == null || documentId.isBlank()) {
            throw new IllegalArgumentException("Document ID cannot be empty");
        }

        EmployeeDocument document = employeeDocumentRepository
                .findByDocumentId(documentId)
                .orElseThrow(() ->
                        new DocumentNotFoundException(
                                "Document not found with ID : " + documentId));

        if ("VERIFIED".equalsIgnoreCase(document.getVerificationStatus())) {
            throw new DocumentAlreadyVerifiedException(
                    "Document is already verified");
        }

        if ("REJECTED".equalsIgnoreCase(document.getVerificationStatus())) {
            throw new InvalidDocumentStatusException(
                    "Rejected document cannot be verified");
        }

        String oldStatus = document.getVerificationStatus();

        document.setVerificationStatus("VERIFIED");
        document.setStatus("ACTIVE");
        document.setVerifiedBy(1L);
        document.setVerifiedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());

        EmployeeDocument savedDocument =
                employeeDocumentRepository.save(document);

        saveAudit(
                savedDocument,
                ActionType.VERIFY,
                oldStatus,
                "VERIFIED",
                request
        );

        return mapToResponse(savedDocument);
    }

    @Override
    @Transactional
    public DocumentResponse rejectDocument(
            String documentId,
            String reason,
            HttpServletRequest request) {

        if (documentId == null || documentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Document ID cannot be empty");
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "Rejection reason cannot be empty");
        }

        EmployeeDocument document =
                employeeDocumentRepository.findByDocumentId(documentId)
                        .orElseThrow(() ->
                                new DocumentNotFoundException(
                                        "Document not found with ID : "
                                                + documentId));

        if ("REJECTED".equalsIgnoreCase(
                document.getVerificationStatus())) {

            throw new DocumentAlreadyRejectedException(
                    "Document is already rejected");
        }

        if ("VERIFIED".equalsIgnoreCase(
                document.getVerificationStatus())) {

            throw new InvalidDocumentStatusException(
                    "Verified document cannot be rejected");
        }

        String oldStatus = document.getVerificationStatus();

        document.setVerificationStatus("REJECTED");
        document.setStatus("REJECTED");
        document.setUpdatedAt(LocalDateTime.now());

        EmployeeDocument savedDocument =
                employeeDocumentRepository.save(document);

        saveAudit(
                savedDocument,
                ActionType.REJECT,
                oldStatus,
                "REJECTED : " + reason,
                request
        );

        return mapToResponse(savedDocument);
    }

    private void saveAudit(
            EmployeeDocument document,
            ActionType action,
            String oldValue,
            String newValue,
            HttpServletRequest request
    ) {

        EmployeeDocumentAudit audit =
                EmployeeDocumentAudit.builder()
                        .documentId(document.getId())
                        .employeeId(document.getEmployeeId())
                        .actionType(action)
                        .fileName(document.getFileName())
                        .fileUrl(document.getFileUrl())
                        .remarks(action.name())
                        .performedBy("SYSTEM")
                        .performedAt(LocalDateTime.now())
                        .status(document.getStatus())
                        .oldValue(oldValue)
                        .newValue(newValue)
                        .ipAddress(request.getRemoteAddr())
                        .userAgent(request.getHeader("User-Agent"))
                        .build();

        documentAuditRepository.save(audit);
    }

    private DocumentResponse mapToResponse(
            EmployeeDocument document
    ) {

        return DocumentResponse.builder()
                .documentId(document.getDocumentId())
                .employeeId(document.getEmployeeId())
                .documentType(document.getDocumentType())
                .category(document.getDocumentCategory())
                .title(document.getTitle())
                .fileUrl(document.getFileUrl())
                .fileName(document.getFileName())
                .fileSize(document.getFileSize())
                .version(document.getVersion())
                .issueDate(document.getIssueDate())
                .isPrimary(document.getIsPrimary())
                .expiryDate(document.getExpiryDate())
                .status(document.getStatus())
                .verificationStatus(document.getVerificationStatus())
                .tags(Arrays.asList(document.getTags()))
                .uploadedAt(document.getUploadedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
