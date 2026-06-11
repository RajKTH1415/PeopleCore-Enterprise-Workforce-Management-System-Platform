package com.peoplecore.controller;

import com.peoplecore.dto.response.ApprovalDashboardResponse;
import com.peoplecore.dto.response.ApprovalStatisticsResponse;
import com.peoplecore.service.DocumentApprovalService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/documents/approval/dashboard")
public class DocumentApprovalDashboardController {

    private final DocumentApprovalService documentApprovalService;

    public DocumentApprovalDashboardController(DocumentApprovalService documentApprovalService) {
        this.documentApprovalService = documentApprovalService;
    }


    @GetMapping
    public ResponseEntity<ApiResponse<ApprovalDashboardResponse>> getApprovalDashboard(HttpServletRequest httpServletRequest) {
        ApprovalDashboardResponse response = documentApprovalService.getApprovalDashboard(httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval dashboard fetched successfully",httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<ApprovalStatisticsResponse>> getApprovalStatistics(HttpServletRequest httpServletRequest) {
        ApprovalStatisticsResponse response = documentApprovalService.getApprovalStatistics(httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Approval statistics fetched successfully", httpServletRequest.getRequestURI(), response));
    }
}
