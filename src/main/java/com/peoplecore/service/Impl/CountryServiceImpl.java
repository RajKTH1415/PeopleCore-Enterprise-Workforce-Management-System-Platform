package com.peoplecore.service.Impl;

import com.peoplecore.dto.request.CountryRequest;
import com.peoplecore.dto.response.CountryResponse;
import com.peoplecore.exception.CountryAlreadyExistsException;
import com.peoplecore.exception.ResourceNotFoundException;
import com.peoplecore.module.CountryMaster;
import com.peoplecore.repository.CountryRepository;
import com.peoplecore.service.CountryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public CountryResponse createCountry(CountryRequest request) {

        if (countryRepository.existsByCode(request.getCode().trim().toUpperCase())) {
            throw new CountryAlreadyExistsException(
                    "Country with code " + request.getCode() + " already exists"
            );
        }

        CountryMaster country = CountryMaster.builder()
                .code(request.getCode().trim().toUpperCase())
                .name(request.getName().trim())
                .dialCode(request.getDialCode())
                .currencyCode(
                        request.getCurrencyCode() != null
                                ? request.getCurrencyCode().toUpperCase()
                                : null
                )
                .build();

        CountryMaster savedCountry = countryRepository.save(country);

        return mapToResponse(savedCountry);
    }

    @Override
    public List<CountryResponse> getAllCountries() {

        List<CountryMaster> countries = countryRepository.findAll();

        if (countries.isEmpty()) {
            throw new ResourceNotFoundException("No countries found");
        }

        return countries.stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public CountryResponse getCountryById(Long id) {

        CountryMaster country = countryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Country not found with id: " + id
                        ));

        return mapToResponse(country);
    }
    @Override
    public CountryResponse updateCountry(Long id, CountryRequest request) {

        CountryMaster country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found"));

        if (!country.getCode().equals(request.getCode())
                && countryRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Country code already exists");
        }

        country.setCode(request.getCode());
        country.setName(request.getName());
        country.setDialCode(request.getDialCode());
        country.setCurrencyCode(request.getCurrencyCode());

        country.setUpdatedBy("SYSTEM");

        CountryMaster updatedCountry = countryRepository.save(country);

        return mapToResponse(updatedCountry);
    }

    @Override
    public void deleteCountry(Long id) {

        CountryMaster country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));

        country.setIsActive(false);
        country.setUpdatedBy("SYSTEM");
        country.setUpdatedDate(LocalDateTime.now());

        countryRepository.save(country);
    }

    @Override
    public void deleteAllCountries() {

        if (countryRepository.count() == 0) {
            throw new RuntimeException("No countries available to delete");
        }

        countryRepository.deleteAllInBatch();
    }


    private CountryResponse mapToResponse(
            CountryMaster country) {

        return CountryResponse.builder()
                .code(country.getCode())
                .name(country.getName())
                .dialCode(country.getDialCode())
                .currencyCode(country.getCurrencyCode())
                .isActive(country.getIsActive())
                .createdDate(country.getCreatedDate())
                .createdBy(country.getCreatedBy())
                .updatedDate(country.getUpdatedDate())
                .updatedBy(country.getUpdatedBy())
                .build();
    }
}