package com.peoplecore.service;


import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.*;
import com.peoplecore.module.DocumentApprovalWorkflow;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface DocumentApprovalService {

    DocumentApprovalResponse requestApproval(
            String documentId,
            HttpServletRequest request
    );

    DocumentApprovalResponse approveDocument(
            Long approvalId,
            HttpServletRequest request
    );

    DocumentApprovalResponse rejectApproval(
            Long approvalId,
            String reason,
            HttpServletRequest request
    );
    PageResponse<DocumentApprovalResponse> getPendingApprovals(
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    );

    PageResponse<DocumentApprovalResponse> getApprovalHistory(
            String documentId,
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    );

    DocumentApprovalResponse getApprovalById(Long approvalId , HttpServletRequest httpServletRequest);

    DocumentApprovalResponse cancelApproval(
            Long approvalId,
            HttpServletRequest request
    );

    PageResponse<DocumentApprovalResponse> getApprovalsByStatus(
            String status,
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    );

    ApprovalDashboardResponse getApprovalDashboard(
            HttpServletRequest request);

    PageResponse<DocumentApprovalResponse> getMyApprovals(
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    );

    PageResponse<DocumentApprovalResponse> getMyPendingActions(
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    );

    List<DocumentApprovalResponse> bulkApprove(
            BulkApprovalRequest request,
            HttpServletRequest httpServletRequest
    );

    List<DocumentApprovalResponse> bulkReject(
            BulkRejectRequest request,
            HttpServletRequest httpServletRequest
    );

    PageResponse<ApprovalAuditLogResponse> getApprovalAuditLogs(
            Long approvalId,
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    );

    DocumentApprovalResponse updateApprovalRemarks(
            Long approvalId,
            ApprovalRemarksRequest request,
            HttpServletRequest httpServletRequest
    );
    DocumentApprovalResponse escalateApproval(
            Long approvalId,
            ApprovalEscalationRequest request,
            HttpServletRequest httpServletRequest
    );
    ApprovalStatisticsResponse getApprovalStatistics(
            HttpServletRequest httpServletRequest
    );

}