package com.peoplecore.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressVerificationRequestResponse {

    private Long id;
    private Long addressId;
    private String employeeId;
    private String verificationStatus;
    private String requestedBy;
    private LocalDateTime requestedDate;
    private String verificationMethod;
    private String verificationNotes;
}
