package com.peoplecore.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "employee_document_skill_mapping")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDocumentSkillMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_skill_id")
    private Long employeeSkillId;

    @Column(name = "source", length = 30)
    private String source;


    @Column(name = "confidence_score")
    private BigDecimal confidenceScore;

    @Column(name = "verification_status")
    private String verificationStatus;

    @Column(name = "created_by")
    private String createdBy;
}
