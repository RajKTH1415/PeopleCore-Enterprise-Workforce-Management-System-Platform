package com.peoplecore.controller;

import com.peoplecore.dto.request.ApprovalWorkflowRequest;
import com.peoplecore.module.DocumentApprovalWorkflow;
import com.peoplecore.service.DocumentApprovalService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents/workflow")
public class DocumentWorkflowController {


    private final DocumentApprovalService documentApprovalService;

    public DocumentWorkflowController(DocumentApprovalService documentApprovalService) {
        this.documentApprovalService = documentApprovalService;
    }

    @PostMapping("/{documentId}/assign-approval-workflow")
    public ResponseEntity<ApiResponse<List<DocumentApprovalWorkflow>>> assignApprovalWorkflow(@PathVariable String documentId, @RequestBody ApprovalWorkflowRequest requestBody, HttpServletRequest httpServletRequest) {
        List<DocumentApprovalWorkflow> response = documentApprovalService.assignApprovalWorkflow(documentId, requestBody, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval workflow assigned successfully", httpServletRequest.getRequestURI(), response));
    }
}
