package com.peoplecore.module;

import com.peoplecore.enums.EmploymentStatus;
import com.peoplecore.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Employee  extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,name = "employee_id")
    private String employeeId;

    @Column(name = "user_id",unique = true,nullable = false)
    private Integer userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name",nullable = false)
    private String lastName;

    @Column(name = "email",unique = true,nullable = false)
    private String email;

    @Column(name = "mobile",unique = true,nullable = false)
    private String mobileNumber;

    @Column(name = "designation", nullable = false)
    private String designation;

    @Column(name = "department",nullable = false)
    private String department;


    @Column(name = "joining_date",nullable = false)
    private LocalDate joiningDate;
    @Column(name = "exit_date")
    private LocalDate exitDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;


    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;


    @Column(name = "probation_end_date")
    private LocalDate probationEndDate;
    @Column(name = "confirmation_date")
    private LocalDate confirmationDate;

    @Column(name = "notice_start_date")
    private LocalDate noticeStartDate;

    @Column(columnDefinition = "TEXT")
    private String terminationReason;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;

    // One manager → Many employees
    @JsonIgnore
    @OneToMany(mappedBy = "manager")
    private List<Employee> subordinates;



}
