package com.peoplecore.controller;
import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.AddressHistoryResponse;
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
@RequestMapping("/api/v1/address")
public class AddressManagementController {


    private final AddressManagementService addressManagementService;

    public AddressManagementController(AddressManagementService addressManagementService) {
        this.addressManagementService = addressManagementService;
    }

    @PostMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(@PathVariable Long employeeId, @RequestBody AddressRequest request, HttpServletRequest httpServletRequest) {
        AddressResponse response = addressManagementService.addAddress(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Address added successfully",httpServletRequest.getRequestURI(), response));
    }
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getEmployeeAddresses(@PathVariable Long employeeId, HttpServletRequest httpServletRequest) {
        List<AddressResponse> response = addressManagementService.getAddressesByEmployeeId(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Employee addresses fetched successfully", httpServletRequest.getRequestURI(), response));
    }
    @GetMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(@PathVariable Long addressId, HttpServletRequest httpServletRequest) {
        AddressResponse response = addressManagementService.getAddressById(addressId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Address fetched successfully", httpServletRequest.getRequestURI(), response));
    }
    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(@PathVariable Long addressId, @RequestBody UpdateAddressRequest request, HttpServletRequest httpServletRequest) {
        AddressResponse response = addressManagementService.updateAddress(addressId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Address updated successfully", httpServletRequest.getRequestURI(), response));
    }

    @PatchMapping("/{addressId}/primary")
    public ResponseEntity<ApiResponse<AddressResponse>> setPrimaryAddress(@PathVariable Long addressId, HttpServletRequest request) {
        AddressResponse response = addressManagementService.setPrimaryAddress(addressId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Primary address updated successfully", request.getRequestURI(), response));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<String>> deleteAddress(@PathVariable Long addressId, HttpServletRequest request) {
        addressManagementService.deleteAddress(addressId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Address deleted successfully", request.getRequestURI(), null));
    }

    @PatchMapping("/{addressId}/verify")
    public ResponseEntity<ApiResponse<AddressResponse>> verifyAddress(@PathVariable Long addressId, @RequestBody VerifyAddressRequest request, HttpServletRequest httpServletRequest) {
        AddressResponse response = addressManagementService.verifyAddress(addressId, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Address verified successfully", httpServletRequest.getRequestURI(), response));
    }
    @GetMapping("/{addressId}/history")
    public ResponseEntity<ApiResponse<List<AddressHistoryResponse>>> getAddressHistory(@PathVariable Long addressId, HttpServletRequest request) {
        List<AddressHistoryResponse> response = addressManagementService.getAddressHistory(addressId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Address history fetched successfully", request.getRequestURI(), response));
    }

    @DeleteMapping("/{addressId}/permanent")
    public ResponseEntity<ApiResponse<String>> permanentDeleteAddress(@PathVariable Long addressId, HttpServletRequest request) {
        addressManagementService.permanentDeleteAddress(addressId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Address permanently deleted successfully", request.getRequestURI(), null));
    }
    @PatchMapping("/{addressId}/restore")
    public ResponseEntity<ApiResponse<AddressResponse>> restoreAddress(@PathVariable Long addressId, HttpServletRequest request) {
        AddressResponse response = addressManagementService.restoreAddress(addressId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Address restored successfully", request.getRequestURI(), response));
    }
    @PostMapping("/{addressId}/verification-request")
    public ResponseEntity<ApiResponse<AddressVerificationRequestResponse>> createVerificationRequest(@PathVariable Long addressId, @RequestBody CreateVerificationRequest request, HttpServletRequest httpServletRequest) {
        AddressVerificationRequestResponse response = addressManagementService.createRequest(addressId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Verification request created successfully", httpServletRequest.getRequestURI(), response));
    }
    @PatchMapping("/verification-request/{requestId}/complete")
    public ResponseEntity<ApiResponse<AddressVerificationRequestResponse>> completeVerificationRequest(@PathVariable Long requestId, @RequestBody CompleteVerificationRequest request, HttpServletRequest httpServletRequest) {
        AddressVerificationRequestResponse response = addressManagementService.completeVerificationRequest(requestId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Verification request completed successfully", httpServletRequest.getRequestURI(), response));
    }

    @PatchMapping("/verification-request/{requestId}/assign")
    public ResponseEntity<ApiResponse<AddressVerificationRequestResponse>> assignVerificationRequest(@PathVariable Long requestId, @RequestBody AssignVerificationRequest request, HttpServletRequest httpServletRequest) {
        AddressVerificationRequestResponse response = addressManagementService.assignVerificationRequest(requestId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Verification request assigned successfully", httpServletRequest.getRequestURI(), response));
    }
    @GetMapping("/employee/{employeeId}/primary")
    public ResponseEntity<ApiResponse<AddressResponse>> getPrimaryAddress(@PathVariable Long employeeId, HttpServletRequest httpServletRequest) {
        AddressResponse response = addressManagementService.getPrimaryAddress(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Primary address fetched successfully", httpServletRequest.getRequestURI(), response));
    }
    @GetMapping("/verification-request/{requestId}")
    public ResponseEntity<ApiResponse<AddressVerificationRequestResponse>> getVerificationRequestById(@PathVariable Long requestId, HttpServletRequest httpServletRequest) {
        AddressVerificationRequestResponse response = addressManagementService.getVerificationRequestById(requestId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Verification request fetched successfully", httpServletRequest.getRequestURI(), response));
    }
    @GetMapping("/verification-request")
    public ResponseEntity<ApiResponse<List<AddressVerificationRequestResponse>>> getAllVerificationRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) String employeeId,
            HttpServletRequest httpServletRequest) {

        List<AddressVerificationRequestResponse> response = addressManagementService.getAllVerificationRequests(
                        status,
                        assignedTo,
                        employeeId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Verification requests fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/pending-verification")
    public ResponseEntity<ApiResponse<List<AddressVerificationRequestResponse>>> getPendingVerificationRequests(HttpServletRequest httpServletRequest) {
        List<AddressVerificationRequestResponse> response = addressManagementService.getPendingVerificationRequests();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Pending verification requests fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/employee/{employeeId}/type/{addressType}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressByType(
            @PathVariable Long employeeId,
            @PathVariable String addressType,
            HttpServletRequest httpServletRequest) {

        AddressResponse response = addressManagementService.getAddressByType(employeeId, addressType);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Address fetched by type successfully", httpServletRequest.getRequestURI(), response));
    }
}
