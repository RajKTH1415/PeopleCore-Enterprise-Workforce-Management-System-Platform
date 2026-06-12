package com.peoplecore.dto.request;
import lombok.Data;

@Data
public class UpdateWorkflowRequest {

    private Integer approvalLevel;

    private Long approverId;

    private String roleName;

    private String workflowStatus;
}
