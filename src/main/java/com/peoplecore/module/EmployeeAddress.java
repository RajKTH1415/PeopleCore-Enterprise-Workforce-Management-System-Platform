package com.peoplecore.module;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "employee_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_employee_address_employee")
    )
    private Employee employee;


    @Column(name = "address_type", nullable = false, length = 30)
    private String addressType;

    @Column(name = "address_line1", nullable = false, length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "landmark", length = 100)
    private String landmark;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "city_id",
            foreignKey = @ForeignKey(name = "fk_employee_address_city")
    )
    private CityMaster cityMaster;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "state_id",
            foreignKey = @ForeignKey(name = "fk_employee_address_state")
    )
    private StateMaster stateMaster;

    @Column(name = "pincode", nullable = false, length = 10)
    private String pincode;

    @Column(name = "country", length = 100)
    @Builder.Default
    private String country = "India";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "country_id",
            foreignKey = @ForeignKey(name = "fk_employee_address_country")
    )
    private CountryMaster countryMaster;


    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "google_place_id", length = 255)
    private String googlePlaceId;

    // =========================================
    // Verification
    // =========================================

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "verified_by",
            foreignKey = @ForeignKey(name = "fk_employee_address_verified_by")
    )
    private Employee verifiedBy;

    @Column(name = "verified_date")
    private LocalDateTime verifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "verification_document_id",
            foreignKey = @ForeignKey(name = "fk_employee_address_document")
    )
    private EmployeeDocument verificationDocument;

    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;

    // Status
    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;


    // Validity
    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "residence_type", length = 30)
    private String residenceType;

    @Column(name = "stay_since")
    private LocalDate staySince;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Audit Fields
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    // Lifecycle Hooks
    @PrePersist
    public void prePersist() {

        this.createdDate = LocalDateTime.now();

        if (this.createdBy == null) {
            this.createdBy = "SYSTEM";
        }

        if (this.country == null) {
            this.country = "India";
        }

        if (this.isActive == null) {
            this.isActive = true;
        }

        if (this.isDeleted == null) {
            this.isDeleted = false;
        }

        if (this.isVerified == null) {
            this.isVerified = false;
        }

        if (this.isPrimary == null) {
            this.isPrimary = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
