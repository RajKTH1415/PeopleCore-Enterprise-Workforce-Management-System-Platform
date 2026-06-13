package com.peoplecore.controller;
import com.peoplecore.dto.request.ApprovalWorkflowRequest;
import com.peoplecore.dto.request.UpdateWorkflowRequest;
import com.peoplecore.dto.request.WorkflowTemplateRequest;
import com.peoplecore.module.DocumentApprovalWorkflow;
import com.peoplecore.module.WorkflowTemplate;
import com.peoplecore.service.DocumentWorkflowService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents/workflow")
public class DocumentWorkflowController {


    private final DocumentWorkflowService documentWorkflowService;

    public DocumentWorkflowController(DocumentWorkflowService documentWorkflowService) {
        this.documentWorkflowService = documentWorkflowService;
    }

    @PostMapping("/{documentId}/assign-approval-workflow")
    public ResponseEntity<ApiResponse<List<DocumentApprovalWorkflow>>> assignApprovalWorkflow(@PathVariable String documentId, @RequestBody ApprovalWorkflowRequest requestBody, HttpServletRequest httpServletRequest) {
        List<DocumentApprovalWorkflow> response = documentWorkflowService.assignApprovalWorkflow(documentId, requestBody, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval workflow assigned successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<List<DocumentApprovalWorkflow>>> getWorkflowByDocumentId(@PathVariable String documentId, HttpServletRequest httpServletRequest) {
        List<DocumentApprovalWorkflow> response = documentWorkflowService.getWorkflowByDocumentId(documentId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Workflow fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @PutMapping("/{workflowId}")
    public ResponseEntity<ApiResponse<DocumentApprovalWorkflow>> updateWorkflow(@PathVariable Long workflowId, @RequestBody UpdateWorkflowRequest request, HttpServletRequest httpServletRequest) {
        DocumentApprovalWorkflow response = documentWorkflowService.updateWorkflow(workflowId, request, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Workflow updated successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<WorkflowTemplate>>> getTemplates(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Workflow templates fetched successfully", request.getRequestURI(), documentWorkflowService.getWorkflowTemplates()));
    }

    @DeleteMapping("/{workflowId}")
    public ResponseEntity<ApiResponse<Void>> deleteWorkflow(@PathVariable Long workflowId, HttpServletRequest request) {
        documentWorkflowService.deleteWorkflow(workflowId, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Workflow deleted successfully", request.getRequestURI(), null));
    }
    @PostMapping("/templates")
    public ResponseEntity<ApiResponse<WorkflowTemplate>> createTemplate(@RequestBody WorkflowTemplateRequest request, HttpServletRequest httpServletRequest) {
        WorkflowTemplate response = documentWorkflowService.createWorkflowTemplate(request, httpServletRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Workflow template created successfully", httpServletRequest.getRequestURI(), response));
    }
}
