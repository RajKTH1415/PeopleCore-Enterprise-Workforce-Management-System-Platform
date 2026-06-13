package com.peoplecore.controller;

import com.peoplecore.dto.request.VerifyAddressRequest;
import com.peoplecore.dto.response.AddressResponse;
import com.peoplecore.service.AddressManagementService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
