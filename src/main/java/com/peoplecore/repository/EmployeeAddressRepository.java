package com.peoplecore.repository;

import com.peoplecore.module.EmployeeAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeAddressRepository
        extends JpaRepository<EmployeeAddress, Long> {


    Optional<EmployeeAddress>
    findByIdAndIsDeletedTrue(Long addressId);

    List<EmployeeAddress> findByEmployeeIdAndIsDeletedFalse(Long employeeId);

    Optional<EmployeeAddress> findByEmployeeIdAndIsPrimaryTrue(Long employeeId);
    Optional<EmployeeAddress> findByEmployee_IdAndAddressType(
            Long employeeId,
            String addressType
    );



    List<EmployeeAddress> findByEmployeeId(Long employeeId);


    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
       UPDATE EmployeeAddress ea
       SET ea.isPrimary = false
       WHERE ea.employee.id = :employeeId
       """)
    void removePrimaryAddress(
            @Param("employeeId") Long employeeId
    );

    Optional<EmployeeAddress>
    findFirstByEmployeeIdAndIsDeletedFalseAndIsActiveTrueOrderByCreatedDateAsc(
            Long employeeId
    );



}
