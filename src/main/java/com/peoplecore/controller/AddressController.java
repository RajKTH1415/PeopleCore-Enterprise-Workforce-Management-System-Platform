package com.peoplecore.controller;
import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.AddressResponse;
import com.peoplecore.service.AddressManagementService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {


    private final AddressManagementService addressManagementService;

    public AddressController(AddressManagementService addressManagementService) {
        this.addressManagementService = addressManagementService;
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

}
