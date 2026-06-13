package com.peoplecore.controller;

import com.peoplecore.dto.request.VerifyAddressRequest;
import com.peoplecore.dto.response.AddressResponse;
import com.peoplecore.dto.response.AddressVerificationRequestResponse;
import com.peoplecore.service.AddressManagementService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/address-verifications")
public class AddressVerificationController {

    private final AddressManagementService addressManagementService;

    public AddressVerificationController(AddressManagementService addressManagementService) {
        this.addressManagementService = addressManagementService;
    }

    @PatchMapping("/{addressId}/verify")
    public ResponseEntity<ApiResponse<AddressResponse>> verifyAddress(@PathVariable Long addressId, @RequestBody VerifyAddressRequest request, HttpServletRequest httpServletRequest) {
        AddressResponse response = addressManagementService.verifyAddress(addressId, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Address verified successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/pending-verification")
    public ResponseEntity<ApiResponse<List<AddressVerificationRequestResponse>>> getPendingVerificationRequests(HttpServletRequest httpServletRequest) {
        List<AddressVerificationRequestResponse> response = addressManagementService.getPendingVerificationRequests();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Pending verification requests fetched successfully", httpServletRequest.getRequestURI(), response));
    }
}
