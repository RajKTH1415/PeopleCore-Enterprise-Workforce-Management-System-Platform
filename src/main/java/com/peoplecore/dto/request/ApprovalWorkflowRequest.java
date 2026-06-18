package com.peoplecore.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ApprovalWorkflowRequest {

    @NotEmpty(message = "Workflow levels cannot be empty")
    @Valid
    private List<WorkflowLevelRequest> workflowLevels;
}
