package com.peoplecore.service.Impl;
import com.peoplecore.dto.request.ApprovalWorkflowRequest;
import com.peoplecore.dto.request.UpdateWorkflowRequest;
import com.peoplecore.dto.request.WorkflowTemplateRequest;
import com.peoplecore.exception.BadRequestException;
import com.peoplecore.exception.ResourceNotFoundException;
import com.peoplecore.module.ApprovalAuditLog;
import com.peoplecore.module.DocumentApprovalWorkflow;
import com.peoplecore.module.WorkflowTemplate;
import com.peoplecore.repository.*;
import com.peoplecore.service.DocumentWorkflowService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentWorkflowServiceImpl implements DocumentWorkflowService {


    private final WorkflowTemplateRepository workflowTemplateRepository;
    private final ApprovalAuditLogRepository approvalAuditLogRepository;
    private final DocumentApprovalWorkflowRepository documentApprovalWorkflowRepository;
    private final EmployeeDocumentRepository employeeDocumentRepository;

    public DocumentWorkflowServiceImpl(WorkflowTemplateRepository workflowTemplateRepository, ApprovalAuditLogRepository approvalAuditLogRepository, DocumentApprovalWorkflowRepository documentApprovalWorkflowRepository, EmployeeDocumentRepository employeeDocumentRepository) {
        this.workflowTemplateRepository = workflowTemplateRepository;
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

        if (documentId == null || documentId.isBlank()) {
            throw new BadRequestException("Document id cannot be null or empty");
        }

        if (request == null ||
                request.getWorkflowLevels() == null ||
                request.getWorkflowLevels().isEmpty()) {

            throw new BadRequestException(
                    "Workflow levels are required");
        }

        LocalDateTime currentTime = LocalDateTime.now();

        List<DocumentApprovalWorkflow> workflows =
                request.getWorkflowLevels()
                        .stream()
                        .map(level -> {

                            if (level.getApprovalLevel() == null) {
                                throw new BadRequestException(
                                        "Approval level cannot be null");
                            }

                            if (level.getApproverId() == null) {
                                throw new BadRequestException(
                                        "Approver id cannot be null");
                            }

                            return DocumentApprovalWorkflow.builder()
                                    .documentId(documentId)
                                    .approvalLevel(level.getApprovalLevel())
                                    .approverId(level.getApproverId())
                                    .roleName(level.getRoleName())
                                    .workflowStatus(
                                            level.getApprovalLevel() == 1
                                                    ? "PENDING"
                                                    : "WAITING"
                                    )
                                    .assignedAt(currentTime)
                                    .build();
                        })
                        .toList();

        List<DocumentApprovalWorkflow> savedWorkflows =
                documentApprovalWorkflowRepository.saveAll(workflows);

        ApprovalAuditLog auditLog =
                ApprovalAuditLog.builder()
                        .documentId(documentId)
                        .action("WORKFLOW_ASSIGNED")
                        .newStatus("WORKFLOW_CREATED")
                        .actionBy(1L)
                        .actionAt(currentTime)
                        .remarks("Multi-level approval workflow assigned")
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

    @Override
    @Transactional
    public DocumentApprovalWorkflow updateWorkflow(
            Long workflowId,
            UpdateWorkflowRequest request,
            HttpServletRequest httpServletRequest
    ) {

        DocumentApprovalWorkflow workflow =
                documentApprovalWorkflowRepository.findById(workflowId)
                        .orElseThrow(() ->
                                new RuntimeException("Workflow not found"));

        workflow.setApprovalLevel(
                request.getApprovalLevel()
        );

        workflow.setApproverId(
                request.getApproverId()
        );

        workflow.setRoleName(
                request.getRoleName()
        );

        workflow.setWorkflowStatus(
                request.getWorkflowStatus()
        );

        DocumentApprovalWorkflow updatedWorkflow =
                documentApprovalWorkflowRepository.save(workflow);

        ApprovalAuditLog auditLog =
                ApprovalAuditLog.builder()
                        .documentId(workflow.getDocumentId())
                        .action("WORKFLOW_UPDATED")
                        .oldStatus("UPDATED")
                        .newStatus(request.getWorkflowStatus())
                        .actionBy(1L)
                        .actionAt(LocalDateTime.now())
                        .remarks(
                                "Workflow updated. Workflow Id : "
                                        + workflowId
                        )
                        .build();

        approvalAuditLogRepository.save(auditLog);

        return updatedWorkflow;
    }

    @Override
    public List<WorkflowTemplate> getWorkflowTemplates() {

        return workflowTemplateRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteWorkflow(
            Long workflowId,
            HttpServletRequest request
    ) {

        DocumentApprovalWorkflow workflow =
                documentApprovalWorkflowRepository.findById(workflowId)
                        .orElseThrow(() ->
                                new RuntimeException("Workflow not found"));

        documentApprovalWorkflowRepository.delete(workflow);

        ApprovalAuditLog auditLog =
                ApprovalAuditLog.builder()
                        .documentId(workflow.getDocumentId())
                        .action("WORKFLOW_DELETED")
                        .oldStatus(workflow.getWorkflowStatus())
                        .newStatus("DELETED")
                        .actionBy(1L)
                        .actionAt(LocalDateTime.now())
                        .remarks("Workflow deleted")
                        .build();

        approvalAuditLogRepository.save(auditLog);
    }

    @Override
    @Transactional
    public WorkflowTemplate createWorkflowTemplate(
            WorkflowTemplateRequest request,
            HttpServletRequest httpServletRequest
    ) {

        WorkflowTemplate template =
                WorkflowTemplate.builder()
                        .templateName(
                                request.getTemplateName()
                        )
                        .description(
                                request.getDescription()
                        )
                        .active(true)
                        .build();

        return workflowTemplateRepository.save(template);
    }
}
