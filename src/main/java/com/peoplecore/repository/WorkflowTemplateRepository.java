package com.peoplecore.repository;

import com.peoplecore.module.WorkflowTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowTemplateRepository
        extends JpaRepository<WorkflowTemplate, Long> {
}
