package com.peoplecore.controller;
import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.EmployeeResponse;
import com.peoplecore.service.EmployeeService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@RequestBody EmployeeRequest employeeRequest, HttpServletRequest httpServletRequest){
        EmployeeResponse employeeResponse = employeeService.createEmployee(employeeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.OK.value(), "Employee created successfully", httpServletRequest.getRequestURI(), employeeResponse));
    }

    @PutMapping("/updateEmployee/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(@PathVariable("employeeId") String employeeId , @RequestBody UpdateEmployeeRequest updateEmployeeRequest, HttpServletRequest httpServletRequest){
        EmployeeResponse updatedEmployeeResponse = employeeService.updateEmployee(employeeId, updateEmployeeRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee updated successfully", httpServletRequest.getRequestURI(), updatedEmployeeResponse));
    }
    @GetMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable String employeeId ,  @RequestParam(value = "include", required = false) String include , HttpServletRequest httpServletRequest){
        EmployeeResponse employeeResponse = employeeService.getEmployeeById(employeeId , include);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee fetched  successfully", httpServletRequest.getRequestURI(), employeeResponse));
    }
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> softDeleteEmployee(@PathVariable("employeeId") String employeeId , HttpServletRequest httpServletRequest){
        EmployeeResponse employeeResponse = employeeService.deleteEmployeeById(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee deleted successfully", httpServletRequest.getRequestURI(), employeeResponse));
    }

}
