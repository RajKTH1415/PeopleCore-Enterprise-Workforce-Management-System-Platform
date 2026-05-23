package com.peoplecore.controller;

import com.peoplecore.dto.request.StateRequest;
import com.peoplecore.dto.response.StateResponse;
import com.peoplecore.service.StateService;
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
@RequestMapping("/api/v1/states")
public class StateController {

    private final StateService stateService;

    public StateController(StateService stateService) {
        this.stateService = stateService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StateResponse>> createState(@Valid @RequestBody StateRequest stateRequest, HttpServletRequest httpServletRequest) {
        StateResponse stateResponse = stateService.createState(stateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "State created successfully", httpServletRequest.getRequestURI(), stateResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StateResponse>>> getAllStates(HttpServletRequest httpServletRequest) {
        List<StateResponse> states = stateService.getAllStates();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "States fetched successfully", httpServletRequest.getRequestURI(), states));
    }

    @GetMapping("/country/{countryId}")
    public ResponseEntity<ApiResponse<List<StateResponse>>> getStatesByCountry(@PathVariable @Positive(message = "Country id must be greater than 0") Long countryId, HttpServletRequest httpServletRequest) {
        List<StateResponse> states = stateService.getStatesByCountry(countryId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "States fetched successfully", httpServletRequest.getRequestURI(), states));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StateResponse>> updateState(@PathVariable Long id, @RequestBody StateRequest stateRequest, HttpServletRequest httpServletRequest) {
        StateResponse stateResponse = stateService.updateState(id, stateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "State updated successfully", httpServletRequest.getRequestURI(), stateResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteState(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        stateService.deleteState(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "State deleted successfully", httpServletRequest.getRequestURI(), null));
    }
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteAllStates(HttpServletRequest httpServletRequest) {
        stateService.deleteAllStates();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "All states deleted successfully", httpServletRequest.getRequestURI(), null));
    }
}