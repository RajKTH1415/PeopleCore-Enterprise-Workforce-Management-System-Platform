package com.peoplecore.controller;
import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.*;
import com.peoplecore.service.DocumentApprovalService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents/approval")
@RequiredArgsConstructor
public class DocumentApprovalController {

    private final DocumentApprovalService documentApprovalService;

    @PostMapping("/{documentId}/request")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> requestApproval(@PathVariable String documentId, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.requestApproval(documentId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Approval request created successfully", httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/{approvalId}/approve")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> approveDocument(@PathVariable Long approvalId, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.approveDocument(approvalId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Document approved successfully", httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/{approvalId}/reject")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> rejectApproval(@PathVariable Long approvalId, @RequestParam String reason, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.rejectApproval(approvalId, reason, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval rejected successfully", httpServletRequest.getRequestURI(), response));
    }


    @PostMapping("/{approvalId}/cancel")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> cancelApproval(@PathVariable Long approvalId, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.cancelApproval(approvalId, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(),"Approval request cancelled successfully", httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/bulk-approve")
    public ResponseEntity<ApiResponse<List<DocumentApprovalResponse>>> bulkApprove(@RequestBody BulkApprovalRequest requestBody, HttpServletRequest httpServletRequest) {
        List<DocumentApprovalResponse> response = documentApprovalService.bulkApprove(requestBody, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Bulk approvals completed successfully", httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/bulk-reject")
    public ResponseEntity<ApiResponse<List<DocumentApprovalResponse>>> bulkReject(@RequestBody BulkRejectRequest requestBody, HttpServletRequest httpServletRequest) {
        List<DocumentApprovalResponse> response = documentApprovalService.bulkReject(requestBody, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Bulk rejection completed successfully", httpServletRequest.getRequestURI(), response));
    }

    @PatchMapping("/approval/{approvalId}/remarks")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> updateApprovalRemarks(

            @PathVariable Long approvalId,

            @RequestBody ApprovalRemarksRequest requestBody,

            HttpServletRequest httpServletRequest) {

        DocumentApprovalResponse response = documentApprovalService.updateApprovalRemarks(approvalId, requestBody, httpServletRequest);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval remarks updated successfully", httpServletRequest.getRequestURI(), response));
    }
    @PostMapping("/{approvalId}/escalate")
    public ResponseEntity<ApiResponse<DocumentApprovalResponse>> escalateApproval(@PathVariable Long approvalId, @RequestBody ApprovalEscalationRequest requestBody, HttpServletRequest httpServletRequest) {
        DocumentApprovalResponse response = documentApprovalService.escalateApproval(approvalId, requestBody, httpServletRequest);
       return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval escalated successfully", httpServletRequest.getRequestURI(), response));
    }

}