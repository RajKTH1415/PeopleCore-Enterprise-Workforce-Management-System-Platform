package com.peoplecore.controller;

import com.peoplecore.dto.response.AddressHistoryResponse;
import com.peoplecore.service.AddressManagementService;
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
@RequestMapping("/api/v1/address-history")
public class AddressHistoryController {

    private final AddressManagementService addressManagementService;

    public AddressHistoryController(AddressManagementService addressManagementService) {
        this.addressManagementService = addressManagementService;
    }

    @GetMapping("/address/{addressId}")
    public ResponseEntity<ApiResponse<List<AddressHistoryResponse>>> getAddressHistory(@PathVariable Long addressId, HttpServletRequest request) {
        List<AddressHistoryResponse> response = addressManagementService.getAddressHistory(addressId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Address history fetched successfully", request.getRequestURI(), response));
    }
}
