package com.peoplecore.controller;

import com.peoplecore.dto.response.EmployeeHierarchyResponse;
import com.peoplecore.dto.response.EmployeeResponse;
import com.peoplecore.service.EmployeeService;
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
@RequestMapping("/api/v1/employees/hierarchy")
public class EmployeeHierarchyController {

    private final EmployeeService employeeService;

    public EmployeeHierarchyController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @GetMapping("/{employeeId}/manager")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getManager(@PathVariable String employeeId, HttpServletRequest httpServletRequest){
        EmployeeResponse employeeResponse = employeeService.getManager(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Manager fetched successfully", httpServletRequest.getRequestURI(), employeeResponse));
    }
    @GetMapping("/{employeeId}/subordinates")
    public ResponseEntity<ApiResponse<List<EmployeeResponse.Subordinate>>> getSubordinates(@PathVariable String employeeId, HttpServletRequest httpServletRequest) {
        List<EmployeeResponse.Subordinate> subOrdinateResponse = employeeService.getSubordinates(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Subordinates fetched successfully", httpServletRequest.getRequestURI(), subOrdinateResponse));
    }
    @GetMapping("/{employeeId}/subordinates/all")
    public ResponseEntity<ApiResponse<List<EmployeeResponse.Subordinate>>> getAllSubordinates(@PathVariable String employeeId, HttpServletRequest httpServletRequest) {
        List<EmployeeResponse.Subordinate> subordinates = employeeService.getAllSubordinates(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Subordinates fetched successfully", httpServletRequest.getRequestURI(), subordinates ));
    }
    @GetMapping("/{employeeId}/hierarchy")
    public ResponseEntity<ApiResponse<EmployeeHierarchyResponse>> getHierarchy(@PathVariable("employeeId") String id, HttpServletRequest httpServletRequest) {
        EmployeeHierarchyResponse employeeHierarchyResponse=employeeService.getEmployeeHierarchy(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "GetHierarchy fetch  successfully",httpServletRequest.getRequestURI(),employeeHierarchyResponse));
    }
}
