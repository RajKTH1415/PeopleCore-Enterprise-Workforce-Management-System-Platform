package com.peoplecore.controller;

import com.peoplecore.dto.request.AssignVerificationRequest;
import com.peoplecore.dto.request.CompleteVerificationRequest;
import com.peoplecore.dto.request.CreateVerificationRequest;
import com.peoplecore.dto.response.AddressVerificationRequestResponse;
import com.peoplecore.service.AddressManagementService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/address-verification-requests")
public class AddressVerificationRequestController {

    private final AddressManagementService addressManagementService;

    public AddressVerificationRequestController(AddressManagementService addressManagementService) {
        this.addressManagementService = addressManagementService;
    }

    @PostMapping("/address/{addressId}")
    public ResponseEntity<ApiResponse<AddressVerificationRequestResponse>> createVerificationRequest(@PathVariable Long addressId, @RequestBody CreateVerificationRequest request, HttpServletRequest httpServletRequest) {
        AddressVerificationRequestResponse response = addressManagementService.createRequest(addressId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Verification request created successfully", httpServletRequest.getRequestURI(), response));
    }

    @PatchMapping("/{requestId}/assign")
    public ResponseEntity<ApiResponse<AddressVerificationRequestResponse>> assignVerificationRequest(@PathVariable Long requestId, @RequestBody AssignVerificationRequest request, HttpServletRequest httpServletRequest) {
        AddressVerificationRequestResponse response = addressManagementService.assignVerificationRequest(requestId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Verification request assigned successfully", httpServletRequest.getRequestURI(), response));
    }

    @PatchMapping("/{requestId}/complete")
    public ResponseEntity<ApiResponse<AddressVerificationRequestResponse>> completeVerificationRequest(@PathVariable Long requestId, @RequestBody CompleteVerificationRequest request, HttpServletRequest httpServletRequest) {
        AddressVerificationRequestResponse response = addressManagementService.completeVerificationRequest(requestId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Verification request completed successfully", httpServletRequest.getRequestURI(), response));
    }
}
