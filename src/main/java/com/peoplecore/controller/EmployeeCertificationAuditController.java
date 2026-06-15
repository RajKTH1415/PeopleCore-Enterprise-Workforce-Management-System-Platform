package com.peoplecore.controller;
import com.peoplecore.dto.response.CertificationVerificationResponse;
import com.peoplecore.dto.response.EmployeeCertificationAuditResponse;
import com.peoplecore.service.EmployeeCertificationAuditService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeCertificationAuditController {

    private final EmployeeCertificationAuditService employeeCertificationAuditService;

    public EmployeeCertificationAuditController(EmployeeCertificationAuditService employeeCertificationAuditService) {
        this.employeeCertificationAuditService = employeeCertificationAuditService;
    }

    @GetMapping("/{employeeId}/certifications/{certificationId}/history")
    public ResponseEntity<ApiResponse<List<EmployeeCertificationAuditResponse>>>
    getCertificationHistory(@PathVariable Long employeeId, @PathVariable Long certificationId, HttpServletRequest httpServletRequest) {
        List<EmployeeCertificationAuditResponse> response = employeeCertificationAuditService.getCertificationHistory(employeeId, certificationId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification history fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{employeeId}/certifications/{certificationId}/verification")
    public ResponseEntity<ApiResponse<CertificationVerificationResponse>> getVerificationDetails(@PathVariable Long employeeId, @PathVariable Long certificationId, HttpServletRequest request) {
        CertificationVerificationResponse response = employeeCertificationAuditService.getVerificationDetails(employeeId, certificationId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification verification details fetched successfully", request.getRequestURI(), response));
    }
}
