package com.peoplecore.module;

import com.peoplecore.constants.CertificationAuditActions;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Builder
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee_certification_audit")
public class EmployeeCertificationAudit {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "certification_id")
    private Long certificationId;

    @Column(name = "action", length = 50)
    private String action;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_type", length = 100)
    private String fileType;

    @Lob
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "file_data")
    private byte[] fileData;

    @Column(name = "file_url", length = 1000)
    private String fileUrl;

    @Column(name = "performed_by", length = 255)
    private String performedBy;

    @Column(name = "performed_at")
    private LocalDateTime performedAt;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @PrePersist
    public void prePersist() {
        if (performedAt == null) {
            performedAt = LocalDateTime.now();
        }
    }
}
