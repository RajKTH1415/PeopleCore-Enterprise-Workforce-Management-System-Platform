package com.peoplecore.service;

import com.peoplecore.dto.request.ApprovalWorkflowRequest;
import com.peoplecore.dto.request.UpdateWorkflowRequest;
import com.peoplecore.module.DocumentApprovalWorkflow;
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

}
