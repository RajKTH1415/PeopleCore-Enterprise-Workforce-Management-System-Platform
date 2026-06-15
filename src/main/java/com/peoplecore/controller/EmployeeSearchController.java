package com.peoplecore.controller;
import com.peoplecore.dto.response.EmployeeResponse;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.enums.Status;
import com.peoplecore.service.EmployeeService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees/search")
public class EmployeeSearchController {

    private final EmployeeService employeeService;

    public EmployeeSearchController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }


    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String managerId,
            @RequestParam(required = false) String search,
            HttpServletRequest httpServletRequest) {

        PageResponse<EmployeeResponse> response = employeeService.getAllEmployees(
                page, size, sortBy, direction, status, department, designation, managerId, search);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "All employees fetched successfully", httpServletRequest.getRequestURI(), response));
    }
}
