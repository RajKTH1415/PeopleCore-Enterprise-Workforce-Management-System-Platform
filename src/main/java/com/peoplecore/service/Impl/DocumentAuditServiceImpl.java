package com.peoplecore.service.Impl;

import com.peoplecore.dto.response.*;
import com.peoplecore.enums.ActionType;
import com.peoplecore.exception.ResourceNotFoundException;
import com.peoplecore.module.DocumentAccessLog;
import com.peoplecore.module.DocumentVersionHistory;
import com.peoplecore.module.EmployeeDocumentAudit;
import com.peoplecore.repository.DocumentAccessLogRepository;
import com.peoplecore.repository.DocumentAuditRepository;
import com.peoplecore.repository.DocumentVersionRepository;
import com.peoplecore.service.DocumentAuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentAuditServiceImpl implements DocumentAuditService {

    private final DocumentVersionRepository documentVersionRepository;
    private final DocumentAuditRepository documentAuditRepository;
    private final DocumentAccessLogRepository documentAccessLogRepository;

    public DocumentAuditServiceImpl(DocumentVersionRepository documentVersionRepository, DocumentAuditRepository documentAuditRepository, DocumentAccessLogRepository documentAccessLogRepository) {
        this.documentVersionRepository = documentVersionRepository;
        this.documentAuditRepository = documentAuditRepository;
        this.documentAccessLogRepository = documentAccessLogRepository;
    }


    @Override
    public PageResponse<EmployeeDocumentAuditResponse> getAuditLogs(
            Long documentId,
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    ) {

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EmployeeDocumentAudit> auditLogs =
                documentAuditRepository.findByDocumentId(
                        documentId,
                        pageable
                );

        Page<EmployeeDocumentAuditResponse> response =
                auditLogs.map(log ->
                        EmployeeDocumentAuditResponse.builder()
                                .id(log.getId())
                                .documentId(log.getDocumentId())
                                .employeeId(log.getEmployeeId())
                                .actionType(log.getActionType())
                                .fileName(log.getFileName())
                                .fileUrl(log.getFileUrl())
                                .remarks(log.getRemarks())
                                .performedBy(log.getPerformedBy())
                                .performedAt(log.getPerformedAt())
                                .actionType(log.getActionType())
                                .status(log.getStatus())
                                .build());

        return PageResponse.<EmployeeDocumentAuditResponse>builder()
                .content(response.getContent())
                .page(response.getNumber())
                .size(response.getSize())
                .totalElements(response.getTotalElements())
                .totalPages(response.getTotalPages())
                .last(response.isLast())
                .build();
    }

    @Override
    public PageResponse<DocumentAccessLogResponse> getAccessLogs(
            String documentId,
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    ) {

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DocumentAccessLog> accessLogs =
                documentAccessLogRepository.findByDocumentId(
                        documentId,
                        pageable
                );

        Page<DocumentAccessLogResponse> response =
                accessLogs.map(log ->
                        DocumentAccessLogResponse.builder()
                                .id(log.getId())
                                .documentRefId(log.getDocumentRefId())
                                .documentId(log.getDocumentId())
                                .accessedBy(log.getAccessedBy())
                                .accessType(log.getAccessType())
                                .accessedAt(log.getAccessedAt())
                                .ipAddress(log.getIpAddress())
                                .userAgent(log.getUserAgent())
                                .build());

        return PageResponse.<DocumentAccessLogResponse>builder()
                .content(response.getContent())
                .page(response.getNumber())
                .size(response.getSize())
                .totalElements(response.getTotalElements())
                .totalPages(response.getTotalPages())
                .last(response.isLast())
                .build();
    }

    @Override
    public List<DocumentVersionResponse> getDocumentVersions(
            String documentId) {

        List<DocumentVersionHistory> versions =
                documentVersionRepository
                        .findByDocumentIdOrderByVersionDesc(documentId);

        if (versions.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No version history found for document : "
                            + documentId);
        }

        return versions.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public List<DocumentVerificationHistoryResponse> getVerificationHistory(Long documentId) {

        List<String> actions = List.of(
                "VERIFY",
                "REJECT"
        );

        List<EmployeeDocumentAudit> audits =
                documentAuditRepository.findByDocumentIdAndActionTypeInOrderByPerformedAtDesc(
                        documentId,
                        actions
                );

        return audits.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<DocumentDownloadHistoryResponse> getDownloadHistory(Long documentId) {

        List<EmployeeDocumentAudit> logs =
                documentAuditRepository.findByDocumentIdAndActionTypeOrderByPerformedAtDesc(
                        documentId,
                        ActionType.DOWNLOAD
                );

        return logs.stream()
                .map(audit -> DocumentDownloadHistoryResponse.builder()
                        .id(audit.getId())
                        .documentId(audit.getDocumentId())
                        .employeeId(audit.getEmployeeId())
                        .actionType(audit.getActionType().name())
                        .fileName(audit.getFileName())
                        .fileUrl(audit.getFileUrl())
                        .performedBy(audit.getPerformedBy())
                        .performedAt(audit.getPerformedAt())
                        .ipAddress(audit.getIpAddress())
                        .userAgent(audit.getUserAgent())
                        .status(audit.getStatus())
                        .remarks(audit.getRemarks())
                        .build()
                )
                .toList();
    }

    @Override
    public List<DocumentRestoreHistoryResponse> getRestoreHistory(Long documentId) {

        List<EmployeeDocumentAudit> logs = documentAuditRepository  .findByDocumentIdAndActionTypeOrderByPerformedAtDesc(
                documentId,
                ActionType.RESTORE
        );

        return logs.stream()
                .map(audit -> DocumentRestoreHistoryResponse.builder()
                        .id(audit.getId())
                        .documentId(audit.getDocumentId())
                        .employeeId(audit.getEmployeeId())
                        .actionType(audit.getActionType().name())
                        .fileName(audit.getFileName())
                        .fileUrl(audit.getFileUrl())
                        .performedBy(audit.getPerformedBy())
                        .performedAt(audit.getPerformedAt())
                        .ipAddress(audit.getIpAddress())
                        .userAgent(audit.getUserAgent())
                        .status(audit.getStatus())
                        .remarks(audit.getRemarks())
                        .oldValue(audit.getOldValue())
                        .newValue(audit.getNewValue())
                        .build())
                .toList();
    }


    private DocumentVerificationHistoryResponse mapToResponse(
            EmployeeDocumentAudit audit) {

        return DocumentVerificationHistoryResponse.builder()
                .id(audit.getId())
                .documentId(audit.getDocumentId())
                .employeeId(audit.getEmployeeId())
                .actionType(audit.getActionType().name())
                .status(audit.getStatus())
                .remarks(audit.getRemarks())
                .performedBy(audit.getPerformedBy())
                .performedAt(audit.getPerformedAt())
                .ipAddress(audit.getIpAddress())
                .oldValue(audit.getOldValue())
                .newValue(audit.getNewValue())
                .build();
    }

    private DocumentVersionResponse convertToResponse(
            DocumentVersionHistory version) {

        return DocumentVersionResponse.builder()
                .id(version.getId())
                .documentId(version.getDocumentId())
                .version(version.getVersion())
                .fileName(version.getFileName())
                .fileSize(version.getFileSize())
                .storageKey(version.getStorageKey())
                .versionComment(version.getVersionComment())
                .uploadedBy(version.getUploadedBy())
                .uploadedAt(version.getUploadedAt())
                .build();
    }
}
