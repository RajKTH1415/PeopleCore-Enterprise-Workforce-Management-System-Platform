package com.peoplecore.service.Impl;

import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.AddressHistoryResponse;
import com.peoplecore.dto.response.AddressResponse;
import com.peoplecore.dto.response.AddressVerificationRequestResponse;
import com.peoplecore.dto.response.GeocodeResponse;
import com.peoplecore.exception.BadRequestException;
import com.peoplecore.exception.ResourceNotFoundException;
import com.peoplecore.module.*;
import com.peoplecore.repository.*;
import com.peoplecore.service.AddressManagementService;
import com.peoplecore.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Slf4j
@Service
@Transactional
public class AddressManagementServiceImpl implements AddressManagementService {

    private final AddressVerificationRequestRepository addressVerificationRequestRepository;
    private final EmployeeDocumentRepository employeeDocumentRepository;
    private final EmployeeAddressRepository employeeAddressRepository;
    private final EmployeeRepository employeeRepository;
    private final AddressHistoryRepository addressHistoryRepository;
    private final GeocodingService geocodingService;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;

    public AddressManagementServiceImpl(AddressVerificationRequestRepository addressVerificationRequestRepository, EmployeeDocumentRepository employeeDocumentRepository, EmployeeAddressRepository employeeAddressRepository, EmployeeRepository employeeRepository, AddressHistoryRepository addressHistoryRepository, GeocodingService geocodingService, CountryRepository countryRepository, StateRepository stateRepository, CityRepository cityRepository) {
        this.addressVerificationRequestRepository = addressVerificationRequestRepository;
        this.employeeDocumentRepository = employeeDocumentRepository;
        this.employeeAddressRepository = employeeAddressRepository;
        this.employeeRepository = employeeRepository;
        this.addressHistoryRepository = addressHistoryRepository;
        this.geocodingService = geocodingService;
        this.countryRepository = countryRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
    }

    @Override
    public AddressResponse addAddress(Long employeeId, AddressRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found with id : " + employeeId));
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            employeeAddressRepository
                    .findByEmployeeIdAndIsPrimaryTrue(employeeId)
                    .ifPresent(existing -> {

                        existing.setIsPrimary(false);

                        employeeAddressRepository.save(existing);
                    });
        }

        CountryMaster country =
                countryRepository
                        .findByNameIgnoreCase(request.getCountry())
                        .orElse(null);

        StateMaster state = null;

        if (country != null) {

            state = stateRepository
                    .findByNameIgnoreCaseAndCountryId(
                            request.getState(),
                            country.getId()
                    )
                    .orElse(null);
        }

        CityMaster city = null;

        if (state != null) {

            city = cityRepository
                    .findByNameIgnoreCaseAndStateId(
                            request.getCity(),
                            state.getId()
                    )
                    .orElse(null);
        }

        EmployeeAddress address = EmployeeAddress.builder()
                .employee(employee)
                .addressType(request.getAddressType())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .landmark(request.getLandmark())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .pincode(request.getPincode())
                .cityMaster(city)
                .stateMaster(state)
                .countryMaster(country)
                .isPrimary(
                        request.getIsPrimary() != null
                                ? request.getIsPrimary()
                                : false
                )
                .isActive(true)
                .isDeleted(false)
                .isVerified(false)
                .effectiveFrom(request.getEffectiveFrom())
                .effectiveTo(request.getEffectiveTo())
                .residenceType(request.getResidenceType())
                .staySince(request.getStaySince())
                .updatedBy("SYSTEM")
                .updatedDate(LocalDateTime.now())
                .notes(request.getNotes())
                .build();

        String fullAddress = String.join(
                ", ",
                request.getAddressLine1(),
                request.getCity(),
                request.getState(),
                request.getPincode(),
                request.getCountry()
        );

        GeocodeResponse geocode =
                geocodingService.geocodeAddress(fullAddress);

        if (geocode != null) {

            address.setLatitude(geocode.getLatitude());

            address.setLongitude(geocode.getLongitude());

            address.setGooglePlaceId(geocode.getPlaceId());
        }

        EmployeeAddress savedAddress =
                employeeAddressRepository.save(address);

        Map<String, Object> newValue = new HashMap<>();

        newValue.put("addressType", savedAddress.getAddressType());
        newValue.put("addressLine1", savedAddress.getAddressLine1());
        newValue.put("addressLine2", savedAddress.getAddressLine2());
        newValue.put("landmark", savedAddress.getLandmark());
        newValue.put("city", savedAddress.getCity());
        newValue.put("state", savedAddress.getState());
        newValue.put("pincode", savedAddress.getPincode());
        newValue.put("country", savedAddress.getCountry());
        newValue.put("isPrimary", savedAddress.getIsPrimary());

        saveAddressHistory(
                savedAddress,
                "CREATE",
                null,
                newValue
        );

        return mapToResponse(savedAddress);
    }

    @Override
    public List<AddressResponse> getAddressesByEmployeeId(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found with id: " + employeeId));

        List<EmployeeAddress> addresses =
                employeeAddressRepository.findByEmployeeId(employeeId);

        // 3. Map to DTO
        return addresses.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AddressResponse getAddressById(Long addressId) {

        EmployeeAddress address = employeeAddressRepository.findById(addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with id: " + addressId
                        ));

        return mapToResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(
            Long addressId,
            UpdateAddressRequest request) {

        EmployeeAddress existingAddress =
                employeeAddressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Address not found with id : "
                                                + addressId
                                ));

        Map<String, Object> oldValue =
                buildAddressSnapshot(existingAddress);

        if (Boolean.TRUE.equals(request.getPrimaryAddress())) {

            employeeAddressRepository.removePrimaryAddress(
                    existingAddress.getEmployee().getId()
            );
        }

        CountryMaster country =
                countryRepository.findById(request.getCountryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Country not found"
                                ));

        StateMaster state =
                stateRepository.findById(request.getStateId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "State not found"
                                ));

        CityMaster city =
                cityRepository.findById(request.getCityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "City not found"
                                ));

        existingAddress.setAddressType(request.getAddressType());
        existingAddress.setAddressLine1(request.getAddressLine1());
        existingAddress.setAddressLine2(request.getAddressLine2());
        existingAddress.setLandmark(request.getLandmark());

        existingAddress.setCity(city.getName());
        existingAddress.setState(state.getName());
        existingAddress.setCountry(country.getName());

        existingAddress.setCityMaster(city);
        existingAddress.setStateMaster(state);
        existingAddress.setCountryMaster(country);

        existingAddress.setPincode(request.getPostalCode());
        existingAddress.setIsPrimary(request.getPrimaryAddress());
        existingAddress.setResidenceType(request.getResidenceType());
        existingAddress.setStaySince(request.getStaySince());
        existingAddress.setEffectiveFrom(request.getEffectiveFrom());
        existingAddress.setEffectiveTo(request.getEffectiveTo());
        existingAddress.setNotes(request.getNotes());

        existingAddress.setIsVerified(false);
        existingAddress.setVerifiedDate(null);
        existingAddress.setVerifiedBy(null);

        existingAddress.setUpdatedDate(LocalDateTime.now());
        existingAddress.setUpdatedBy("SYSTEM");

        EmployeeAddress updatedAddress =
                employeeAddressRepository.save(existingAddress);

        Map<String, Object> newValue =
                buildAddressSnapshot(updatedAddress);


        saveAddressHistory(
                updatedAddress,
                "UPDATED",
                oldValue,
                newValue
        );

        return mapToResponse(updatedAddress);
    }
    @Override
    @Transactional
    public AddressResponse setPrimaryAddress(Long addressId) {

        EmployeeAddress address =
                employeeAddressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Address not found with id : "
                                                + addressId
                                ));

        if (Boolean.TRUE.equals(address.getIsPrimary())) {
            return mapToResponse(address);
        }

        Map<String, Object> oldValue =
                buildAddressSnapshot(address);

        Long employeeId =
                address.getEmployee().getId();

        employeeAddressRepository
                .removePrimaryAddress(employeeId);

        address.setIsPrimary(true);

        address.setUpdatedDate(LocalDateTime.now());
        address.setUpdatedBy("SYSTEM");

        EmployeeAddress updatedAddress =
                employeeAddressRepository.save(address);

        Map<String, Object> newValue =
                buildAddressSnapshot(updatedAddress);

        saveAddressHistory(
                updatedAddress,
                "PRIMARY_ADDRESS_CHANGED",
                oldValue,
                newValue
        );

        return mapToResponse(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) {

        EmployeeAddress address =
                employeeAddressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Address not found with id : "
                                                + addressId
                                ));

        if (Boolean.TRUE.equals(address.getIsDeleted())) {

            throw new BadRequestException(
                    "Address already deleted"
            );
        }

        Map<String, Object> oldValue =
                buildAddressSnapshot(address);

        saveAddressHistory(
                address,
                "DELETED",
                oldValue,
                null
        );

        boolean wasPrimary =
                Boolean.TRUE.equals(address.getIsPrimary());

        Long employeeId =
                address.getEmployee().getId();

        address.setIsDeleted(true);
        address.setIsActive(false);
        address.setIsPrimary(false);

        address.setUpdatedDate(LocalDateTime.now());
        address.setUpdatedBy("SYSTEM");

        employeeAddressRepository.save(address);

        if (wasPrimary) {

            Optional<EmployeeAddress> nextPrimary =
                    employeeAddressRepository
                            .findFirstByEmployeeIdAndIsDeletedFalseAndIsActiveTrueOrderByCreatedDateAsc(
                                    employeeId
                            );

            nextPrimary.ifPresent(existing -> {

                existing.setIsPrimary(true);

                employeeAddressRepository.save(existing);
            });
        }
    }

    @Override
    @Transactional
    public AddressResponse verifyAddress(
            Long addressId,
            VerifyAddressRequest request) {

        EmployeeAddress address =
                employeeAddressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Address not found with id : "
                                                + addressId
                                ));

        if (Boolean.TRUE.equals(address.getIsDeleted())) {

            throw new BadRequestException(
                    "Cannot verify deleted address"
            );
        }

        if (Boolean.TRUE.equals(address.getIsVerified())) {

            throw new BadRequestException(
                    "Address already verified"
            );
        }

        Employee verifier =
                employeeRepository.findById(
                        request.getVerifiedBy()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Verifier employee not found"
                        ));

        address.setIsVerified(true);

        address.setVerifiedBy(verifier);

        address.setVerifiedDate(LocalDateTime.now());

        address.setVerificationNotes(
                request.getVerificationNotes()
        );

        address.setUpdatedDate(LocalDateTime.now());

        address.setUpdatedBy(
                verifier.getEmployeeId()
        );

        if (request.getVerificationDocumentId() != null) {

            EmployeeDocument document =
                    employeeDocumentRepository.findById(
                            request.getVerificationDocumentId()
                    ).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Verification document not found"
                            ));

            address.setVerificationDocument(document);
        }

        EmployeeAddress verifiedAddress =
                employeeAddressRepository.save(address);

        Map<String, Object> oldValue =
                buildAddressSnapshot(address);

        Map<String, Object> newValue =
                buildAddressSnapshot(verifiedAddress);

        saveAddressHistory(
                verifiedAddress,
                "VERIFIED",
                oldValue,
                newValue
        );

        return mapToResponse(verifiedAddress);
    }

    @Override
    public List<AddressHistoryResponse> getAddressHistory(Long addressId) {

        List<AddressHistory> historyList =
                addressHistoryRepository
                        .findByAddress_IdOrderByChangedAtDesc(addressId);

        return historyList.stream()
                .map(history -> {

                    List<String> changedFields = getChangedFields(
                            history.getOldValue(),
                            history.getNewValue()
                    );

                    return AddressHistoryResponse.builder()
                            .historyId(history.getId())
                            .addressId(history.getAddress().getId())
                            .employeeId(history.getEmployee().getId())
                            .action(history.getAction())
                            .oldValue(history.getOldValue())
                            .newValue(history.getNewValue())
                            .changedFields(changedFields)
                            .changedAt(history.getChangedAt())
                            .changedBy(history.getChangedBy())
                            .ipAddress(history.getIpAddress())
                            .remarks(history.getRemarks())
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional
    public void permanentDeleteAddress(Long addressId) {

        EmployeeAddress address =
                employeeAddressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Address not found with id : "
                                                + addressId
                                ));

        addressHistoryRepository
                .deleteByAddress_Id(addressId);

        employeeAddressRepository.delete(address);
    }

    @Override
    @Transactional
    public AddressResponse restoreAddress(Long addressId) {

        EmployeeAddress address =
                employeeAddressRepository
                        .findByIdAndIsDeletedTrue(addressId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Deleted address not found with id : "
                                                + addressId
                                ));

        Map<String, Object> oldValue =
                buildAddressSnapshot(address);

        address.setIsDeleted(false);
        address.setIsActive(true);

        address.setUpdatedDate(LocalDateTime.now());
        address.setUpdatedBy("SYSTEM");

        EmployeeAddress restoredAddress =
                employeeAddressRepository.save(address);

        Map<String, Object> newValue =
                buildAddressSnapshot(restoredAddress);

        saveAddressHistory(
                restoredAddress,
                "RESTORED",
                oldValue,
                newValue
        );

        return mapToResponse(restoredAddress);
    }

    @Override
    @Transactional
    public AddressVerificationRequestResponse createRequest(Long addressId, CreateVerificationRequest request) {

        EmployeeAddress address = employeeAddressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Employee requester = employeeRepository.findById(request.getRequestedBy())
                .orElseThrow(() -> new ResourceNotFoundException("Requester not found"));

        AddressVerificationRequest entity = new AddressVerificationRequest();
        entity.setAddress(address);
        entity.setEmployee(address.getEmployee());
        entity.setRequestedBy(requester);
        entity.setVerificationStatus("PENDING");
        entity.setRequestedDate(LocalDateTime.now());
        entity.setVerificationMethod(request.getVerificationMethod());
        entity.setVerificationNotes(request.getVerificationNotes());
        entity.setCreatedDate(LocalDateTime.now());

        if (request.getVerificationDocumentId() != null) {
            EmployeeDocument doc = employeeDocumentRepository.findById(request.getVerificationDocumentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
            entity.setVerificationDocument(doc);
        }

        AddressVerificationRequest saved = addressVerificationRequestRepository.save(entity);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public AddressVerificationRequestResponse completeVerificationRequest(
            Long requestId,
            CompleteVerificationRequest request) {

        AddressVerificationRequest entity =
                addressVerificationRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Verification request not found"));

        Employee completedBy = employeeRepository.findById(request.getCompletedBy())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        entity.setVerificationStatus(request.getVerificationStatus());

        entity.setCompletedBy(completedBy);

        entity.setCompletedDate(LocalDateTime.now());

        entity.setVerificationNotes(request.getVerificationNotes());

        entity.setRejectionReason(request.getRejectionReason());


        if ("VERIFIED".equalsIgnoreCase(request.getVerificationStatus())) {

            EmployeeAddress address = entity.getAddress();

            address.setIsVerified(true);

            employeeAddressRepository.save(address);
        }

        AddressVerificationRequest saved =
                addressVerificationRequestRepository.save(entity);

        return mapToResponse(saved);
    }

    private AddressVerificationRequestResponse mapToResponse(AddressVerificationRequest entity) {

        AddressVerificationRequestResponse res = new AddressVerificationRequestResponse();

        res.setId(entity.getId());
        res.setAddressId(entity.getAddress().getId());
        res.setEmployeeId(entity.getEmployee().getEmployeeId());
        res.setVerificationStatus(entity.getVerificationStatus());
        res.setRequestedBy(entity.getRequestedBy().getEmployeeId());
        res.setRequestedDate(entity.getRequestedDate());
        res.setVerificationMethod(entity.getVerificationMethod());
        res.setVerificationNotes(entity.getVerificationNotes());

        return res;
    }

    private List<String> getChangedFields(
            Map<String, Object> oldValue,
            Map<String, Object> newValue
    ) {

        List<String> changedFields = new ArrayList<>();

        if (oldValue == null || newValue == null) {
            return changedFields;
        }

        for (String key : oldValue.keySet()) {

            Object oldVal = oldValue.get(key);
            Object newVal = newValue.get(key);

            if (!Objects.equals(oldVal, newVal)) {
                changedFields.add(key);
            }
        }

        return changedFields;
    }

    private Map<String, Object> buildAddressSnapshot(
            EmployeeAddress address
    ) {

        Map<String, Object> data = new HashMap<>();

        data.put("addressType", address.getAddressType());
        data.put("addressLine1", address.getAddressLine1());
        data.put("addressLine2", address.getAddressLine2());
        data.put("landmark", address.getLandmark());
        data.put("city", address.getCity());
        data.put("state", address.getState());
        data.put("pincode", address.getPincode());
        data.put("country", address.getCountry());
        data.put("isPrimary", address.getIsPrimary());

        return data;
    }
//
//    private void saveAddressHistory(EmployeeAddress address,
//                                    String action) {
//
//        try {
//
//            Map<String, Object> data = new HashMap<>();
//
//            data.put("addressType", address.getAddressType());
//            data.put("addressLine1", address.getAddressLine1());
//            data.put("addressLine2", address.getAddressLine2());
//            data.put("landmark", address.getLandmark());
//            data.put("city", address.getCity());
//            data.put("state", address.getState());
//            data.put("pincode", address.getPincode());
//            data.put("country", address.getCountry());
//            data.put("isPrimary", address.getIsPrimary());
//
//            AddressHistory history = AddressHistory.builder()
//                    .address(address)
//                    .employee(address.getEmployee())
//                    .action(action)
//                    .newValue(data)
//                    .changedAt(LocalDateTime.now())
//                    .changedBy("SYSTEM")
//                    .remarks(action + " address")
//                    .build();
//
//            addressHistoryRepository.save(history);
//
//        } catch (Exception ex) {
//
//            log.error("Error while saving address history", ex);
//        }
//    }

    private void saveAddressHistory(
            EmployeeAddress address,
            String action,
            Map<String, Object> oldValue,
            Map<String, Object> newValue
    ) {

        try {

            AddressHistory history = AddressHistory.builder()
                    .address(address)
                    .employee(address.getEmployee())
                    .action(action)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .changedAt(LocalDateTime.now())
                    .changedBy("SYSTEM")
                    .remarks(action + " address")
                    .build();

            addressHistoryRepository.save(history);

        } catch (Exception ex) {

            log.error("Error while saving address history", ex);
        }
    }

    private AddressResponse mapToResponse(EmployeeAddress address) {

        return AddressResponse.builder()
                .id(address.getId())
                .employeeId(address.getEmployee().getId())
                .employeeName(
                        address.getEmployee().getFirstName()
                                + " "
                                + address.getEmployee().getLastName()
                )
                .employeeCode(address.getEmployee().getEmployeeId())
                .addressType(address.getAddressType())
                .addressTypeDisplay(
                        address.getAddressType() != null
                                ? address.getAddressType()
                                .replace("_", " ")
                                : null
                )
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .cityId(
                        address.getCityMaster() != null
                                ? address.getCityMaster().getId()
                                : null
                )
                .state(address.getState())
                .stateId(
                        address.getStateMaster() != null
                                ? address.getStateMaster().getId()
                                : null
                )
                .pincode(address.getPincode())
                .country(address.getCountry())
                .countryId(
                        address.getCountryMaster() != null
                                ? address.getCountryMaster().getId()
                                : null
                )
                .fullAddress(buildFormattedAddress(address))
                .formattedAddress(buildFormattedAddress(address))
                .latitude(
                        address.getLatitude() != null
                                ? address.getLatitude().doubleValue()
                                : null
                )
                .longitude(
                        address.getLongitude() != null
                                ? address.getLongitude().doubleValue()
                                : null
                )
                .googlePlaceId(address.getGooglePlaceId())
                .isVerified(address.getIsVerified())
                .verificationStatus(
                        Boolean.TRUE.equals(address.getIsVerified())
                                ? "VERIFIED"
                                : "PENDING"
                )
                .verifiedDate(address.getVerifiedDate())
                .verifiedByName(
                        address.getVerifiedBy() != null
                                ? address.getVerifiedBy().getFirstName()
                                + " "
                                + address.getVerifiedBy().getLastName()
                                : null
                )
                .isPrimary(address.getIsPrimary())
                .isActive(address.getIsActive())
                .effectiveFrom(address.getEffectiveFrom())
                .effectiveTo(address.getEffectiveTo())
                .residenceType(address.getResidenceType())
                .staySince(address.getStaySince())
                .stayDurationMonths(
                        calculateStayDuration(address.getStaySince())
                )
                .notes(address.getNotes())
                .createdDate(address.getCreatedDate())
                .createdBy(address.getCreatedBy())
                .updatedDate(address.getUpdatedDate())
                .updatedBy(address.getUpdatedBy())
                .build();
    }

    private String buildFormattedAddress(EmployeeAddress address) {

        StringBuilder sb = new StringBuilder();

        if (address.getAddressLine1() != null) {
            sb.append(address.getAddressLine1()).append(", ");
        }

        if (address.getAddressLine2() != null) {
            sb.append(address.getAddressLine2()).append(", ");
        }

        if (address.getLandmark() != null) {
            sb.append(address.getLandmark()).append(", ");
        }

        if (address.getCity() != null) {
            sb.append(address.getCity()).append(", ");
        }

        if (address.getState() != null) {
            sb.append(address.getState()).append(", ");
        }

        if (address.getPincode() != null) {
            sb.append(address.getPincode()).append(", ");
        }

        if (address.getCountry() != null) {
            sb.append(address.getCountry());
        }

        return sb.toString().replaceAll(", $", "");
    }

    private Integer calculateStayDuration(LocalDate staySince) {

        if (staySince == null) {
            return null;
        }

        return (int) ChronoUnit.MONTHS
                .between(staySince, LocalDate.now());
    }
}