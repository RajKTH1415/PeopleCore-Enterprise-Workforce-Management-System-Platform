package com.peoplecore.repository;

import com.peoplecore.module.CountryMaster;
import com.peoplecore.module.StateMaster;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<StateMaster, Long> {

    List<StateMaster> findByCountryId(Long countryId);
    boolean existsByCodeAndIdNot(String code, Long id);

    Optional<StateMaster> findByNameIgnoreCase(String state);

    Optional<StateMaster> findByNameIgnoreCaseAndCountryId(String state, Long id);

    List<StateMaster> findByCountry(CountryMaster country);

    boolean existsByCode(@NotBlank(message = "State code is required") @Size(max = 10, message = "State code cannot exceed 10 characters") String code);

    boolean existsBy();
}
