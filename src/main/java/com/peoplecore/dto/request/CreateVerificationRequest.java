package com.peoplecore.dto.request;

import lombok.Data;

@Data
public class CreateVerificationRequest {

    private Long requestedBy;

    private String verificationMethod;

    private String verificationNotes;

    private Long verificationDocumentId;
}