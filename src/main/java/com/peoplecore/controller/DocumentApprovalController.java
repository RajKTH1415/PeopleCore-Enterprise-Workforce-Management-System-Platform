package com.peoplecore.controller;
import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.*;
import com.peoplecore.module.DocumentApprovalWorkflow;
import com.peoplecore.service.DocumentApprovalService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentApprovalController {

    private final DocumentApprovalService documentApprovalService;

    @PostMapping("/{documentId}/request-approval")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> requestApproval(@PathVariable String documentId, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.requestApproval(documentId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Approval request created successfully", httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/approval/{approvalId}/approve")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> approveDocument(@PathVariable Long approvalId, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.approveDocument(approvalId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Document approved successfully", httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/approval/{approvalId}/reject")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> rejectApproval(@PathVariable Long approvalId, @RequestParam String reason, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.rejectApproval(approvalId, reason, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval rejected successfully", httpServletRequest.getRequestURI(), response));
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

    @GetMapping("/approval/{approvalId}")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> getApprovalById(@PathVariable Long approvalId, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.getApprovalById(approvalId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval details fetched successfully",httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/approval/{approvalId}/cancel")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> cancelApproval(@PathVariable Long approvalId, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.cancelApproval(approvalId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(),"Approval request cancelled successfully", httpServletRequest.getRequestURI(), response));
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

    @GetMapping("/approval/dashboard")
    public ResponseEntity<ApiResponse<ApprovalDashboardResponse>> getApprovalDashboard(HttpServletRequest httpServletRequest) {
        ApprovalDashboardResponse response = documentApprovalService.getApprovalDashboard(httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval dashboard fetched successfully",httpServletRequest.getRequestURI(), response));
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

        PageResponse<DocumentApprovalResponse> response = documentApprovalService.getMyPendingActions(
                        page,
                        size,
                        sortBy,
                        direction,
                        httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Pending action items fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/approvals/bulk-approve")
    public ResponseEntity<ApiResponse<List<DocumentApprovalResponse>>> bulkApprove(@RequestBody BulkApprovalRequest requestBody, HttpServletRequest httpServletRequest) {
        List<DocumentApprovalResponse> response = documentApprovalService.bulkApprove(requestBody, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Bulk approvals completed successfully", httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/approvals/bulk-reject")
    public ResponseEntity<ApiResponse<List<DocumentApprovalResponse>>> bulkReject(@RequestBody BulkRejectRequest requestBody, HttpServletRequest httpServletRequest) {
        List<DocumentApprovalResponse> response = documentApprovalService.bulkReject(requestBody, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Bulk rejection completed successfully", httpServletRequest.getRequestURI(), response));
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

    @PatchMapping("/approval/{approvalId}/remarks")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> updateApprovalRemarks(

            @PathVariable Long approvalId,

            @RequestBody ApprovalRemarksRequest requestBody,

            HttpServletRequest httpServletRequest) {

        DocumentApprovalResponse response = documentApprovalService.updateApprovalRemarks(approvalId, requestBody, httpServletRequest);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval remarks updated successfully", httpServletRequest.getRequestURI(), response));
    }
    @PostMapping("/approval/{approvalId}/escalate")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> escalateApproval(@PathVariable Long approvalId, @RequestBody ApprovalEscalationRequest requestBody, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.escalateApproval(approvalId, requestBody, httpServletRequest);
       return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval escalated successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/approval/statistics")
    public ResponseEntity<ApiResponse<ApprovalStatisticsResponse>> getApprovalStatistics(HttpServletRequest httpServletRequest) {
        ApprovalStatisticsResponse response = documentApprovalService.getApprovalStatistics(httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval statistics fetched successfully", httpServletRequest.getRequestURI(), response));
    }


}