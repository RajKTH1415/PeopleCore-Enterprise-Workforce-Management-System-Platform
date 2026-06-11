package com.peoplecore.controller;

import com.peoplecore.dto.response.ApprovalAuditLogResponse;
import com.peoplecore.dto.response.DocumentApprovalResponse;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.service.DocumentApprovalService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documents/approval/query")
public class DocumentApprovalQueryController {

    private final DocumentApprovalService documentApprovalService;

    public DocumentApprovalQueryController(DocumentApprovalService documentApprovalService) {
        this.documentApprovalService = documentApprovalService;
    }


    @GetMapping("/{documentId}/approvals")
    public ResponseEntity<ApiResponse<PageResponse<DocumentApprovalResponse>>> getApprovalHistory(
            @PathVariable String documentId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "requestedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction,

            HttpServletRequest httpServletRequest) {

        PageResponse<DocumentApprovalResponse> response = documentApprovalService.getApprovalHistory(
                documentId,
                page,
                size,
                sortBy,
                direction,
                httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval history fetched successfully", httpServletRequest.getRequestURI(), response));
    }


    @GetMapping("/my-approvals")
    public ResponseEntity<ApiResponse<PageResponse<DocumentApprovalResponse>>> getMyApprovals(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "requestedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction,

            HttpServletRequest httpServletRequest) {

        PageResponse<DocumentApprovalResponse> response = documentApprovalService.getMyApprovals(
                page,
                size,
                sortBy,
                direction,
                httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "My approvals fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/approval/{approvalId}")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> getApprovalById(@PathVariable Long approvalId, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.getApprovalById(approvalId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval details fetched successfully",httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/approvals")
    public ResponseEntity<ApiResponse<PageResponse<DocumentApprovalResponse>>> getApprovalsByStatus(

            @RequestParam String status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "requestedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction,

            HttpServletRequest httpServletRequest) {

        PageResponse<DocumentApprovalResponse> response =
                documentApprovalService.getApprovalsByStatus(
                        status,
                        page,
                        size,
                        sortBy,
                        direction,
                        httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approvals fetched successfully", httpServletRequest.getRequestURI(), response));
    }


    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<PageResponse<DocumentApprovalResponse>>> getPendingApprovals(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "10")
            int size,
            @RequestParam(defaultValue = "requestedAt")
            String sortBy,
            @RequestParam(defaultValue = "DESC")
            String direction,
            HttpServletRequest httpServletRequest) {

        PageResponse<DocumentApprovalResponse> response = documentApprovalService.getPendingApprovals(
                page,
                size,
                sortBy,
                direction,
                httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Pending approvals fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/approval/{approvalId}/audit-logs")
    public ResponseEntity<ApiResponse<PageResponse<ApprovalAuditLogResponse>>> getApprovalAuditLogs(

            @PathVariable Long approvalId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "actionAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction,

            HttpServletRequest httpServletRequest
    ) {

        PageResponse<ApprovalAuditLogResponse> response =
                documentApprovalService.getApprovalAuditLogs(
                        approvalId,
                        page,
                        size,
                        sortBy,
                        direction,
                        httpServletRequest
                );

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval audit logs fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/my-pending-actions")
    public ResponseEntity<ApiResponse<PageResponse<DocumentApprovalResponse>>> getMyPendingActions(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "requestedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction,

            HttpServletRequest httpServletRequest) {

        PageResponse<DocumentApprovalResponse> response =
                documentApprovalService.getMyPendingActions(
                        page,
                        size,
                        sortBy,
                        direction,
                        httpServletRequest
                );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Pending action items fetched successfully",
                        httpServletRequest.getRequestURI(),
                        response
                ));
    }


}
