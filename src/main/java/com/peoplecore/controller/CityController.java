package com.peoplecore.controller;

import com.peoplecore.dto.request.CityRequest;
import com.peoplecore.dto.response.CityResponse;
import com.peoplecore.service.CityService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CityResponse>> createCity(@Valid @RequestBody CityRequest request, HttpServletRequest httpServletRequest) {
        CityResponse response = cityService.createCity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "City created successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CityResponse>>> getAllCities(HttpServletRequest httpServletRequest) {
        List<CityResponse> response = cityService.getAllCities();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Cities fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @Validated
    @GetMapping("/state/{stateId}")
    public ResponseEntity<ApiResponse<List<CityResponse>>> getCitiesByState(@PathVariable  @Min(value = 1, message = "State id must be greater than 0")Long stateId, HttpServletRequest httpServletRequest) {
        List<CityResponse> response = cityService.getCitiesByState(stateId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Cities fetched successfully for state", httpServletRequest.getRequestURI(), response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CityResponse>> updateCity(@PathVariable Long id, @RequestBody CityRequest request, HttpServletRequest httpServletRequest) {
        CityResponse response = cityService.updateCity(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "City updated successfully", httpServletRequest.getRequestURI(), response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCity(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        cityService.deleteCity(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "City deleted successfully", httpServletRequest.getRequestURI(), null));
    }
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteAllCities(HttpServletRequest httpServletRequest) {
        cityService.deleteAllCities();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "All cities deleted successfully", httpServletRequest.getRequestURI(), null));
    }
}
