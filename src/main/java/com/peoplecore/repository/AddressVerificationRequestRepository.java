package com.peoplecore.repository;

import com.peoplecore.enums.VerificationStatus;
import com.peoplecore.module.AddressVerificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressVerificationRequestRepository
        extends JpaRepository<AddressVerificationRequest, Long> {

    List<AddressVerificationRequest>
    findByVerificationStatus(String status);

    @Query("""
       SELECT avr
       FROM AddressVerificationRequest avr
       WHERE (:status IS NULL OR avr.verificationStatus = :status)
       AND (:assignedTo IS NULL OR avr.assignedTo.id = :assignedTo)
       AND (:employeeId IS NULL OR avr.employee.employeeId = :employeeId)
       """)
    List<AddressVerificationRequest> findVerificationRequests(
            @Param("status") String status,
            @Param("assignedTo") Long assignedTo,
            @Param("employeeId") String employeeId
    );

    List<AddressVerificationRequest> findByVerificationStatusOrderByCreatedDateDesc(
            String verificationStatus
    );

}
