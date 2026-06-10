package com.peoplecore.controller;


import com.peoplecore.dto.response.EmployeeDashboardResponse;
import com.peoplecore.service.EmployeeService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees/dashboard")
public class EmployeeDashboardController {

    private final EmployeeService employeeService;

    public EmployeeDashboardController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<EmployeeDashboardResponse>> getEmployeeDashboard(HttpServletRequest httpServletRequest) {
        EmployeeDashboardResponse response = employeeService.getEmployeeDashboard();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee dashboard fetched successfully",httpServletRequest.getRequestURI(), response));
    }
}
