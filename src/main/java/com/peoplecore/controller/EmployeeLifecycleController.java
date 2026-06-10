package com.peoplecore.controller;

import com.peoplecore.dto.request.EmployeeOnboardRequest;
import com.peoplecore.dto.request.NoticePeriodRequest;
import com.peoplecore.dto.request.TerminationRequest;
import com.peoplecore.dto.response.EmployeeResponse;
import com.peoplecore.service.EmployeeService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees/lifecycle")
public class EmployeeLifecycleController {


    private final EmployeeService employeeService;

    public EmployeeLifecycleController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/onboard")
    public ResponseEntity<ApiResponse<EmployeeResponse>> onboardEmployee(@RequestBody EmployeeOnboardRequest employeeOnboardRequest , HttpServletRequest httpServletRequest) {
        EmployeeResponse employeeResponse = employeeService.onboardEmployee(employeeOnboardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.OK.value(), "Employee onboarded successfully", httpServletRequest.getRequestURI(), employeeResponse));
    }

    @PutMapping("/{employeeId}/start-probation")
    public ResponseEntity<ApiResponse<EmployeeResponse>> startProbation(@PathVariable("employeeId") String employeeId, HttpServletRequest httpServletRequest){
        EmployeeResponse employeeResponse = employeeService.startProbation(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee moved to PROBATION", httpServletRequest.getRequestURI(), employeeResponse));
    }
    @PutMapping("/{employeeId}/confirm")
    public ResponseEntity<ApiResponse<EmployeeResponse>> confirmEmployee(@PathVariable String employeeId, HttpServletRequest httpServletRequest) {
        EmployeeResponse response = employeeService.confirmEmployee(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee confirmed successfully", httpServletRequest.getRequestURI(), response));
    }
    @PutMapping("/{employeeId}/notice")
    public ResponseEntity<ApiResponse<EmployeeResponse>> startNoticePeriod(@PathVariable String employeeId, @RequestBody NoticePeriodRequest noticePeriodRequest , HttpServletRequest httpServletRequest){
        EmployeeResponse employeeResponse = employeeService.startNoticePeriod(employeeId, noticePeriodRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee moved to notice period", httpServletRequest.getRequestURI(), employeeResponse));
    }
    @PutMapping("/{employeeId}/exit")
    public ResponseEntity<ApiResponse<EmployeeResponse>> exitEmployee(@PathVariable String employeeId, HttpServletRequest httpServletRequest) {
        EmployeeResponse response = employeeService.exitEmployee(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(),  httpServletRequest.getRequestURI(),"Employee exited successfully",response));
    }
    @PutMapping("/{employeeId}/terminate")
    public ResponseEntity<ApiResponse<EmployeeResponse>> terminateEmployee(@PathVariable String employeeId, @RequestBody TerminationRequest request, HttpServletRequest httpServletRequest) {
        EmployeeResponse response = employeeService.terminateEmployee(employeeId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), httpServletRequest.getRequestURI(),"Employee terminated successfully", response));
    }
}
