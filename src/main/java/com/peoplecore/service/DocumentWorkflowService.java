package com.peoplecore.service;

import com.peoplecore.dto.request.ApprovalWorkflowRequest;
import com.peoplecore.dto.request.UpdateWorkflowRequest;
import com.peoplecore.dto.request.WorkflowTemplateRequest;
import com.peoplecore.module.DocumentApprovalWorkflow;
import com.peoplecore.module.WorkflowTemplate;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface DocumentWorkflowService  {

    List<DocumentApprovalWorkflow> assignApprovalWorkflow(
            String documentId,
            ApprovalWorkflowRequest request,
            HttpServletRequest httpServletRequest
    );

    List<DocumentApprovalWorkflow> getWorkflowByDocumentId(
            String documentId,
            HttpServletRequest request);

    DocumentApprovalWorkflow updateWorkflow(
            Long workflowId,
            UpdateWorkflowRequest request,
            HttpServletRequest httpServletRequest
    );
    List<WorkflowTemplate> getWorkflowTemplates();

    void deleteWorkflow(
            Long workflowId,
            HttpServletRequest request
    );

    WorkflowTemplate createWorkflowTemplate(
            WorkflowTemplateRequest request,
            HttpServletRequest httpServletRequest
    );

}
