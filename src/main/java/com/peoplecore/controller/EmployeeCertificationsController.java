package com.peoplecore.controller;
import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.BulkAssignResponse;
import com.peoplecore.dto.response.EmployeeCertificationResponse;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.service.EmployeeCertificationsService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeCertificationsController {

    private final EmployeeCertificationsService employeeCertificationsService;

    public EmployeeCertificationsController(EmployeeCertificationsService employeeCertificationsService) {
        this.employeeCertificationsService = employeeCertificationsService;
    }

    @PostMapping("/{employeeId}/certifications")
    public ResponseEntity<ApiResponse<EmployeeCertificationResponse>> assignedCertification(
            @PathVariable("employeeId") Long employeeId ,
            @RequestBody AssignCertificationRequest assignCertificationRequest,
            HttpServletRequest httpServletRequest){
        EmployeeCertificationResponse certificationResponse = employeeCertificationsService.assignCertification(employeeId, assignCertificationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Certification assigned successfully", httpServletRequest.getRequestURI(), certificationResponse));
    }

    @PostMapping("/certifications/bulk")
    public ResponseEntity<ApiResponse<BulkAssignResponse>> bulkAssignCertification(
            @RequestBody BulkAssignCertificationRequest request,
            HttpServletRequest httpServletRequest) {

        BulkAssignResponse response = employeeCertificationsService.bulkAssignCertification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(),"Bulk certification assignment completed", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{employeeId}/certifications")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeCertificationResponse>>> getEmployeeCertifications(
            @PathVariable("employeeId") Long empId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "expiryDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest httpServletRequest) {

        PageResponse<EmployeeCertificationResponse> response =
                employeeCertificationsService.getEmployeeCertifications(
                        empId, page, size, status, sortBy, direction);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(),"Employee certifications fetched successfully", httpServletRequest.getRequestURI(), response));

    }
    @PutMapping("/{employeeId}/certifications/{certificationId}")
    public ResponseEntity<ApiResponse<EmployeeCertificationResponse>> updateEmployeeCertification(
            @PathVariable("employeeId") Long empId ,
            @PathVariable("certificationId") Long certiId ,
            @RequestBody UpdateEmployeeCertificationRequest certificationRequest,
            HttpServletRequest httpServletRequest){
         EmployeeCertificationResponse certificationResponse = employeeCertificationsService.updateEmployeeCertification(empId, certiId, certificationRequest);
         return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee certification updated successfully", httpServletRequest.getRequestURI(), certificationResponse));

    }
    @DeleteMapping("/{employeeId}/certifications/{certificationId}")
    public ResponseEntity<ApiResponse<EmployeeCertificationResponse>> removeEmployeeCertification(
            @PathVariable("employeeId") Long empId ,
            @PathVariable("certificationId") Long certiId ,
            HttpServletRequest httpServletRequest){
        EmployeeCertificationResponse certificationResponse = employeeCertificationsService.removeEmployeeCertification(empId, certiId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification removed from employee successfully", httpServletRequest.getRequestURI(), certificationResponse));
    }
    @GetMapping("/certifications/expired")
    public ResponseEntity<ApiResponse<List<EmployeeCertificationResponse>>> getExpiredCertifications(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiredBefore,
            HttpServletRequest httpServletRequest) {

        List<EmployeeCertificationResponse> response =
                employeeCertificationsService.getExpiredCertifications(employeeId, expiredBefore);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(),"Expired certifications fetched", httpServletRequest.getRequestURI(), response));
    }
    @GetMapping("/certifications/expiring-soon")
    public ResponseEntity<ApiResponse<List<EmployeeCertificationResponse>>> getExpiringSoon(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(required = false) Long employeeId,
            HttpServletRequest httpServletRequest) {

        List<EmployeeCertificationResponse> response =
                employeeCertificationsService.getExpiringSoon(days, employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Expiring certifications fetched", httpServletRequest.getRequestURI(), response));
    }

    @PutMapping("/{employeeId}/certifications/{certificationId}/renew")
    public ResponseEntity<ApiResponse<EmployeeCertificationResponse>> renewCertification(
            @PathVariable Long employeeId,
            @PathVariable Long certificationId,
            @RequestBody RenewCertificationRequest request,
            HttpServletRequest httpServletRequest) {

        EmployeeCertificationResponse response =
                employeeCertificationsService.renewCertification(
                        employeeId,
                        certificationId,
                        request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification renewed successfully", httpServletRequest.getRequestURI(), response));
    }

    @PutMapping("/{employeeId}/certifications/{certificationId}/verify")
    public ResponseEntity<ApiResponse<EmployeeCertificationResponse>> verifyCertification(
            @PathVariable Long employeeId,
            @PathVariable Long certificationId,
            HttpServletRequest httpServletRequest) {

        EmployeeCertificationResponse response =
                employeeCertificationsService.verifyCertification(employeeId, certificationId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification verified successfully", httpServletRequest.getRequestURI(), response));
    }

    @PutMapping("/{employeeId}/certifications/{certificationId}/reject")
    public ResponseEntity<ApiResponse<EmployeeCertificationResponse>> rejectCertification(
            @PathVariable Long employeeId,
            @PathVariable Long certificationId,
            HttpServletRequest httpServletRequest) {

        EmployeeCertificationResponse response = employeeCertificationsService.rejectCertification(employeeId, certificationId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification rejected successfully", httpServletRequest.getRequestURI(), response));
    }
}
