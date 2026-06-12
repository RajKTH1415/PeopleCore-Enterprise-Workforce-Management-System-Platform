package com.peoplecore.repository;

import com.peoplecore.module.DocumentVersionHistory;
import com.peoplecore.module.EmployeeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> , JpaSpecificationExecutor {



    Optional<EmployeeDocument> findByEmployeeIdAndFileHash(Long employeeId, String fileHash);


    Optional<EmployeeDocument> findByEmployeeIdAndDocumentId(Long employeeId, String documentId);

    Optional<EmployeeDocument> findByDocumentIdAndIsDeletedFalse(String documentId);

    Optional<EmployeeDocument> findByDocumentId(String documentId);

    @Modifying
    @Query("UPDATE EmployeeDocument d SET d.isPrimary = false WHERE d.employeeId = :employeeId")
    void clearPrimaryForEmployee(@Param("employeeId") Long employeeId);


    @Modifying
    @Query("""
    UPDATE EmployeeDocument d
    SET d.isDeleted = true,
        d.isPrimary = false,
        d.updatedAt = CURRENT_TIMESTAMP
    WHERE d.documentId = :documentId
    AND d.employeeId = :employeeId
    AND (d.isDeleted = false OR d.isDeleted IS NULL)
""")
    int softDelete(@Param("employeeId") Long employeeId,
                   @Param("documentId") String documentId);



    Page<EmployeeDocument> findByExpiryDateBetweenAndIsDeletedFalse(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    Page<EmployeeDocument> findByExpiryDateBeforeAndIsDeletedFalse(
            LocalDate date,
            Pageable pageable
    );

    List<DocumentVersionHistory>
    findByDocumentIdOrderByVersionDesc(
            String documentId);

    Optional<DocumentVersionHistory>
    findByDocumentIdAndVersion(
            String documentId,
            Integer version);


}