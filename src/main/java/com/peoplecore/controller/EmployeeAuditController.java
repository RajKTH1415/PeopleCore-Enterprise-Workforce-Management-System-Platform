package com.peoplecore.controller;

import com.peoplecore.service.EmployeeService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/employees/audit")
public class EmployeeAuditController{


    private final EmployeeService employeeService;

    public EmployeeAuditController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }


    @GetMapping("/{employeeId}/lifecycle-history")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLifecycleHistory(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "changedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest httpServletRequest) {
        Map<String, Object> response = employeeService.getEmployeeLifecycleHistory(
                employeeId, page, size, status, startDate, endDate, sortBy, direction);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee lifecycle history fetched successfully",httpServletRequest.getRequestURI(),response));
    }

}
