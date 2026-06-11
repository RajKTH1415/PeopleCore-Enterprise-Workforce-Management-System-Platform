package com.peoplecore.repository;

import com.peoplecore.module.DocumentVersionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersionHistory, Long> {
    void deleteByDocumentRefId(Long id);
    List<DocumentVersionHistory> findByDocumentRefIdOrderByVersionDesc(Long documentRefId);

    Optional<DocumentVersionHistory> findByDocumentIdAndVersion(String documentId, Integer version);

    List<DocumentVersionHistory> findByDocumentIdOrderByVersionDesc(
            String documentId
    );
}