package com.peoplecore.controller;

import com.peoplecore.dto.request.AddressRequest;
import com.peoplecore.dto.response.AddressResponse;
import com.peoplecore.service.AddressManagementService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees/{employeeId}/addresses")
public class EmployeeAddressController {


    private final AddressManagementService addressManagementService;

    public EmployeeAddressController(AddressManagementService addressManagementService) {
        this.addressManagementService = addressManagementService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(@PathVariable Long employeeId, @RequestBody AddressRequest request, HttpServletRequest httpServletRequest) {
        AddressResponse response = addressManagementService.addAddress(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Address added successfully",httpServletRequest.getRequestURI(), response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getEmployeeAddresses(@PathVariable Long employeeId, HttpServletRequest httpServletRequest) {
        List<AddressResponse> response = addressManagementService.getAddressesByEmployeeId(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee addresses fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/primary")
    public ResponseEntity<ApiResponse<AddressResponse>> getPrimaryAddress(@PathVariable Long employeeId, HttpServletRequest httpServletRequest) {
        AddressResponse response = addressManagementService.getPrimaryAddress(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Primary address fetched successfully", httpServletRequest.getRequestURI(), response));
    }
    @GetMapping("/type/{addressType}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressByType(
            @PathVariable Long employeeId,
            @PathVariable String addressType,
            HttpServletRequest httpServletRequest) {

        AddressResponse response = addressManagementService.getAddressByType(employeeId, addressType);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Address fetched by type successfully", httpServletRequest.getRequestURI(), response));
    }
}
