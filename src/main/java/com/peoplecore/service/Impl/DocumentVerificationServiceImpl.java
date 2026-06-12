package com.peoplecore.service.Impl;

import com.peoplecore.dto.response.DocumentResponse;
import com.peoplecore.enums.ActionType;
import com.peoplecore.module.EmployeeDocument;
import com.peoplecore.module.EmployeeDocumentAudit;
import com.peoplecore.repository.DocumentAuditRepository;
import com.peoplecore.repository.EmployeeDocumentRepository;
import com.peoplecore.service.DocumentVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

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
    public DocumentResponse verifyDocument(String documentId, HttpServletRequest request) {
        EmployeeDocument document = employeeDocumentRepository.findByDocumentId(documentId)
                        .orElseThrow(() ->
                                new RuntimeException("Document not found"));

        String oldStatus = document.getVerificationStatus();

        document.setVerificationStatus("VERIFIED");
        document.setStatus("ACTIVE");
        document.setVerifiedBy(1L);

        document.setVerifiedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());

        EmployeeDocument savedDocument = employeeDocumentRepository.save(document);

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
    public DocumentResponse rejectDocument(
            String documentId,
            String reason,
            HttpServletRequest request
    ) {

        EmployeeDocument document =
                employeeDocumentRepository.findByDocumentId(documentId)
                        .orElseThrow(() ->
                                new RuntimeException("Document not found"));

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
