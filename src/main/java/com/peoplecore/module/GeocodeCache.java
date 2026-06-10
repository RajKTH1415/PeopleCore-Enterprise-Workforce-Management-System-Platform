package com.peoplecore.module;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(
        name = "geocode_cache",
        indexes = {
                @Index(
                        name = "idx_geocode_cache_coordinates",
                        columnList = "latitude, longitude"
                ),
                @Index(
                        name = "idx_geocode_cache_last_used",
                        columnList = "last_used"
                ),
                @Index(
                        name = "idx_geocode_cache_place_id",
                        columnList = "place_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeocodeCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Address Hash
    @Column(name = "address_hash", nullable = false, unique = true, length = 64)
    private String addressHash;

    // Geocode Details
    @Column(name = "formatted_address", columnDefinition = "TEXT")
    private String formattedAddress;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "place_id", length = 255)
    private String placeId;


    // Address Components JSON
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "address_components", columnDefinition = "jsonb")
    private Map<String, Object> addressComponents;


    // Audit Fields
    @Column(name = "last_used", nullable = false)
    private LocalDateTime lastUsed;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    // Lifecycle Hooks
    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        this.createdDate = now;

        if (this.lastUsed == null) {
            this.lastUsed = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUsed = LocalDateTime.now();
    }
}
