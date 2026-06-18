package com.peoplecore.repository;

import com.peoplecore.module.WorkflowTemplate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowTemplateRepository
        extends JpaRepository<WorkflowTemplate, Long> {
    boolean existsByTemplateNameIgnoreCase(@NotBlank(message = "Template name is required") @Size(
            min = 3,
            max = 100,
            message = "Template name must be between 3 and 100 characters"
    ) String templateName);
}
