package com.peoplecore.service;

import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.AddressHistoryResponse;
import com.peoplecore.dto.response.AddressResponse;
import com.peoplecore.dto.response.AddressVerificationRequestResponse;

import java.util.List;

public interface AddressManagementService {


    AddressResponse addAddress(Long employeeId, AddressRequest request);


    List<AddressResponse> getAddressesByEmployeeId(Long employeeId);


    AddressResponse getAddressById(Long addressId);

    AddressResponse updateAddress(
            Long addressId,
            UpdateAddressRequest request
    );

    AddressResponse setPrimaryAddress(Long addressId);

    void deleteAddress(Long addressId);

    AddressResponse verifyAddress(
            Long addressId,
            VerifyAddressRequest request
    );

    List<AddressHistoryResponse> getAddressHistory(Long addressId);


    void permanentDeleteAddress(Long addressId);

    AddressResponse restoreAddress(Long addressId);

    AddressVerificationRequestResponse createRequest(Long addressId, CreateVerificationRequest request);

    AddressVerificationRequestResponse completeVerificationRequest(
            Long requestId,
            CompleteVerificationRequest request
    );
    AddressVerificationRequestResponse assignVerificationRequest(
            Long requestId,
            AssignVerificationRequest request
    );

    AddressResponse getPrimaryAddress(Long employeeId);

    AddressVerificationRequestResponse getVerificationRequestById(Long requestId);

    List<AddressVerificationRequestResponse> getAllVerificationRequests(
            String status,
            Long assignedTo,
            String employeeId
    );
    List<AddressVerificationRequestResponse> getPendingVerificationRequests();
}