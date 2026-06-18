package com.peoplecore.dto.request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateWorkflowRequest {

    @NotNull(message = "Approval level is required")
    @Min(value = 1, message = "Approval level must be greater than 0")
    private Integer approvalLevel;

    @NotNull(message = "Approver id is required")
    private Long approverId;

    @NotBlank(message = "Role name is required")
    private String roleName;

    @NotBlank(message = "Workflow status is required")
    private String workflowStatus;
}
