package com.peoplecore.service.Impl;
import com.peoplecore.dto.request.CityRequest;
import com.peoplecore.dto.response.CityResponse;
import com.peoplecore.exception.NoDataFoundException;
import com.peoplecore.exception.ResourceNotFoundException;
import com.peoplecore.module.CityMaster;
import com.peoplecore.module.StateMaster;
import com.peoplecore.repository.CityRepository;
import com.peoplecore.repository.StateRepository;
import com.peoplecore.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final StateRepository stateRepository;

    public CityServiceImpl(CityRepository cityRepository, StateRepository stateRepository) {
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
    }


    @Override
    public CityResponse createCity(CityRequest request) {

        StateMaster state = stateRepository.findById(request.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "State not found with id: " + request.getStateId()
                ));

        CityMaster city = CityMaster.builder()
                .state(state)
                .code(request.getCode())
                .name(request.getName())
                .pinCode(request.getPinCode())
                .build();

        CityMaster saved = cityRepository.save(city);

        return mapToResponse(saved);
    }

    @Override
    public List<CityResponse> getAllCities() {

        List<CityResponse> cities = cityRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        if (cities.isEmpty()) {
            throw new NoDataFoundException("No cities found");
        }

        return cities;
    }

    @Override
    public List<CityResponse> getCitiesByState(Long stateId) {

        StateMaster state = stateRepository.findById(stateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "State not found with id: " + stateId
                ));

        List<CityResponse> cities = cityRepository.findByState(state)
                .stream()
                .map(this::mapToResponse)
                .toList();

        if (cities.isEmpty()) {
            throw new NoDataFoundException(
                    "No cities found for state id: " + stateId
            );
        }

        return cities;
    }

    @Override
    public CityResponse updateCity(Long id, CityRequest request) {

        CityMaster city = cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));

        StateMaster state = stateRepository.findById(request.getStateId())
                .orElseThrow(() -> new RuntimeException("State not found"));

        city.setState(state);
        city.setCode(request.getCode());
        city.setName(request.getName());
        city.setPinCode(request.getPinCode());

        CityMaster updated = cityRepository.save(city);

        return mapToResponse(updated);
    }

    @Override
    public void deleteCity(Long id) {

        CityMaster city = cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));

        cityRepository.delete(city);
    }

    @Override
    public void deleteAllCities() {

        if (cityRepository.count() == 0) {
            throw new RuntimeException("No cities available to delete");
        }

        cityRepository.deleteAllInBatch();
    }

    private CityResponse mapToResponse(CityMaster city) {

        return CityResponse.builder()
                .id(city.getId())
                .stateId(city.getState().getId())
                .stateName(city.getState().getName())
                .code(city.getCode())
                .name(city.getName())
                .pinCode(city.getPinCode())
                .isActive(city.getIsActive())
                .build();
    }
}
