package com.peoplecore.service.Impl;

import com.peoplecore.dto.request.ApprovalWorkflowRequest;
import com.peoplecore.exception.ResourceNotFoundException;
import com.peoplecore.module.ApprovalAuditLog;
import com.peoplecore.module.DocumentApprovalWorkflow;
import com.peoplecore.repository.ApprovalAuditLogRepository;
import com.peoplecore.repository.DocumentApprovalRepository;
import com.peoplecore.repository.DocumentApprovalWorkflowRepository;
import com.peoplecore.repository.EmployeeDocumentRepository;
import com.peoplecore.service.DocumentWorkflowService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentWorkflowServiceImpl implements DocumentWorkflowService {


    private final DocumentApprovalRepository documentApprovalRepository;
    private final ApprovalAuditLogRepository approvalAuditLogRepository;
    private final DocumentApprovalWorkflowRepository documentApprovalWorkflowRepository;
    private final EmployeeDocumentRepository employeeDocumentRepository;

    public DocumentWorkflowServiceImpl(DocumentApprovalRepository documentApprovalRepository, ApprovalAuditLogRepository approvalAuditLogRepository, DocumentApprovalWorkflowRepository documentApprovalWorkflowRepository, EmployeeDocumentRepository employeeDocumentRepository) {
        this.documentApprovalRepository = documentApprovalRepository;
        this.approvalAuditLogRepository = approvalAuditLogRepository;
        this.documentApprovalWorkflowRepository = documentApprovalWorkflowRepository;
        this.employeeDocumentRepository = employeeDocumentRepository;
    }


    @Override
    @Transactional
    public List<DocumentApprovalWorkflow> assignApprovalWorkflow(
            String documentId,
            ApprovalWorkflowRequest request,
            HttpServletRequest httpServletRequest
    ) {

        List<DocumentApprovalWorkflow> workflows =
                request.getWorkflowLevels()
                        .stream()
                        .map(level ->
                                DocumentApprovalWorkflow.builder()
                                        .documentId(documentId)
                                        .approvalLevel(
                                                level.getApprovalLevel()
                                        )
                                        .approverId(
                                                level.getApproverId()
                                        )
                                        .roleName(
                                                level.getRoleName()
                                        )
                                        .workflowStatus(
                                                level.getApprovalLevel() == 1
                                                        ? "PENDING"
                                                        : "WAITING"
                                        )
                                        .assignedAt(LocalDateTime.now())
                                        .build()
                        )
                        .toList();

        List<DocumentApprovalWorkflow> savedWorkflows =
                documentApprovalWorkflowRepository.saveAll(workflows);

        ApprovalAuditLog auditLog =
                ApprovalAuditLog.builder()
                        .documentId(documentId)
                        .action("WORKFLOW_ASSIGNED")
                        .newStatus("WORKFLOW_CREATED")
                        .actionBy(1L)
                        .actionAt(LocalDateTime.now())
                        .remarks(
                                "Multi-level approval workflow assigned"
                        )
                        .build();

        approvalAuditLogRepository.save(auditLog);

        return savedWorkflows;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentApprovalWorkflow> getWorkflowByDocumentId(
            String documentId,
            HttpServletRequest request) {

        employeeDocumentRepository.findByDocumentId(documentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Document not found with id : " + documentId));

        return documentApprovalWorkflowRepository
                .findByDocumentIdOrderByApprovalLevelAsc(documentId);
    }
}
