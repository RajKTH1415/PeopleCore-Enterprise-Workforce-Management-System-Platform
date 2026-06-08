package com.peoplecore.controller;

import com.peoplecore.dto.request.CountryRequest;
import com.peoplecore.dto.response.CountryResponse;
import com.peoplecore.service.CountryService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CountryResponse>> createCountry(@Valid @RequestBody CountryRequest countryRequest, HttpServletRequest httpServletRequest){
        CountryResponse countryResponse = countryService.createCountry(countryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(),"Country created successfully", httpServletRequest.getRequestURI(), countryResponse));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<CountryResponse>>> getAllCountries(HttpServletRequest httpServletRequest) {
        List<CountryResponse> countries = countryService.getAllCountries();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Countries fetched successfully", httpServletRequest.getRequestURI(), countries));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CountryResponse>> getCountryById(@PathVariable  @Positive(message = "Country id must be greater than zero")Long id, HttpServletRequest httpServletRequest) {
        CountryResponse countryResponse = countryService.getCountryById(id);
       return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Country fetched successfully", httpServletRequest.getRequestURI(), countryResponse));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CountryResponse>> updateCountry(@PathVariable Long id,  @Valid  @RequestBody CountryRequest countryRequest, HttpServletRequest httpServletRequest) {
        CountryResponse countryResponse = countryService.updateCountry(id, countryRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Country updated successfully", httpServletRequest.getRequestURI(), countryResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCountry(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        countryService.deleteCountry(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Country deleted successfully", httpServletRequest.getRequestURI(), null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteAllCountries(HttpServletRequest httpServletRequest) {
        countryService.deleteAllCountries();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "All countries deleted successfully", httpServletRequest.getRequestURI(), null));
    }
}
