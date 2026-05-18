package com.peoplecore.dto.request;


import lombok.Data;

@Data
public class CompleteVerificationRequest {

    private String verificationStatus; // VERIFIED or REJECTED

    private Long completedBy;

    private String verificationNotes;

    private String rejectionReason;
}
