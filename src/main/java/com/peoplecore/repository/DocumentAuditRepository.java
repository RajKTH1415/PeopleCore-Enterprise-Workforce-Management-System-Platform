package com.peoplecore.repository;

import com.peoplecore.enums.ActionType;
import com.peoplecore.module.EmployeeDocumentAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentAuditRepository extends JpaRepository<EmployeeDocumentAudit, Long> {
    void deleteByDocumentId(Long id);


    List<EmployeeDocumentAudit> findByDocumentIdOrderByPerformedAtDesc(Long documentId);

    Page<EmployeeDocumentAudit> findByDocumentId(
            Long documentId,
            Pageable pageable
    );
    List<EmployeeDocumentAudit>
    findByDocumentIdAndActionTypeInOrderByPerformedAtDesc(
            Long documentId,
            List<String> actionTypes
    );

    List<EmployeeDocumentAudit>
    findByDocumentIdAndActionTypeOrderByPerformedAtDesc(
            Long documentId,
            ActionType actionType
    );


}