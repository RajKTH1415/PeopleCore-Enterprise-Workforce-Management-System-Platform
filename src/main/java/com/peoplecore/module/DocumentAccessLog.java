package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_access_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_ref_id", nullable = false)
    private Long documentRefId;

    @Column(name = "document_id", nullable = false, length = 50)
    private String documentId;

    @Column(name = "accessed_by", nullable = false, length = 50)
    private String accessedBy;

    @Column(name = "access_type", nullable = false, length = 20)
    private String accessType;

    @Column(name = "accessed_at")
    private LocalDateTime accessedAt;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
}