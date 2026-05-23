package com.peoplecore.service.Impl;


import com.peoplecore.dto.request.StateRequest;
import com.peoplecore.dto.response.StateResponse;
import com.peoplecore.exception.BadRequestException;
import com.peoplecore.exception.ResourceNotFoundException;
import com.peoplecore.module.CountryMaster;
import com.peoplecore.module.StateMaster;
import com.peoplecore.repository.CountryRepository;
import com.peoplecore.repository.StateRepository;
import com.peoplecore.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateServiceImpl implements StateService {

    private final StateRepository stateRepository;
    private final CountryRepository countryRepository;

    public StateServiceImpl(StateRepository stateRepository, CountryRepository countryRepository) {
        this.stateRepository = stateRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    public StateResponse createState(StateRequest request) {

        CountryMaster country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found"));

        if (stateRepository.existsByCode(request.getCode())) {
            throw new BadRequestException(
                    "State already exists with code : " + request.getCode()
            );
        }

        StateMaster state = StateMaster.builder()
                .country(country)
                .code(request.getCode())
                .name(request.getName())
                .capital(request.getCapital())
                .build();

        StateMaster saved = stateRepository.save(state);

        return mapToResponse(saved);
    }

    @Override
    public List<StateResponse> getAllStates() {

        List<StateMaster> states = stateRepository.findAll();
        if (states.isEmpty()) {
            throw new ResourceNotFoundException("No states found");
        }

        return states.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<StateResponse> getStatesByCountry(Long countryId) {

        CountryMaster country = countryRepository.findById(countryId)
                .orElseThrow(() -> new RuntimeException("Country not found"));

        return stateRepository.findByCountry(country)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // UPDATE STATE
    @Override
    public StateResponse updateState(Long id, StateRequest request) {

        StateMaster state = stateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("State not found"));

        CountryMaster country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new RuntimeException("Country not found"));

        state.setCountry(country);
        state.setCode(request.getCode());
        state.setName(request.getName());
        state.setCapital(request.getCapital());

        StateMaster updated = stateRepository.save(state);

        return mapToResponse(updated);
    }

    @Override
    public void deleteState(Long id) {

        StateMaster state = stateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("State not found"));

        stateRepository.delete(state);
    }

    @Override
    public void deleteAllStates() {

        if (stateRepository.count() == 0) {
            throw new RuntimeException("No states available to delete");
        }

        stateRepository.deleteAllInBatch();
    }

    private StateResponse mapToResponse(StateMaster state) {

        return StateResponse.builder()
                .id(state.getId())
                .countryId(state.getCountry().getId())
                .countryName(state.getCountry().getName())
                .code(state.getCode())
                .name(state.getName())
                .capital(state.getCapital())
                .isActive(state.getIsActive())
                .build();
    }
}