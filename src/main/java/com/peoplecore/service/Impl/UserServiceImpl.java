package com.peoplecore.service.Impl;
import com.peoplecore.dto.request.UpdateUserRequest;
import com.peoplecore.dto.request.UserRequest;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.dto.response.UserResponse;
import com.peoplecore.exception.*;
import com.peoplecore.module.Role;
import com.peoplecore.enums.RoleName;
import com.peoplecore.enums.Status;
import com.peoplecore.module.User;
import com.peoplecore.repository.RoleRepository;
import com.peoplecore.repository.UserRepository;
import com.peoplecore.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserResponse createUser(UserRequest userRequest){
        String email = userRequest.getUserEmail().trim().toLowerCase();
        String userName = userRequest.getUserName().trim();

        Optional<User> existingUser  = userRepository.findExistingUser(
                email , userName);

           if (existingUser.isPresent()) {
               User user = existingUser.get();

               if (user.getUserEmail().equalsIgnoreCase(userRequest.getUserEmail())) {
               throw new DuplicateResourceException("Email already exists");
           }
           if (user.getUserName().equalsIgnoreCase(userRequest.getUserName())) {
               throw new DuplicateResourceException("Username already exists");
           }
       }
           User newUser = new User();
           newUser.setUserID(generateUserId());
           newUser.setUserName(userRequest.getUserName());
           newUser.setUserEmail(userRequest.getUserEmail());
           newUser.setUserPassword(userRequest.getUserPassword());
           newUser.setPasswordChangeDate(LocalDateTime.now());
           newUser.setMobileNumber(userRequest.getMobileNumber());
           newUser.setCity(userRequest.getCity());
           newUser.setCountry(userRequest.getCountry());
           newUser.setCluster(userRequest.getCluster());
           newUser.setState(userRequest.getState());
           newUser.setStatus(Status.ACTIVE);

        Set<Role> roles = new HashSet<>();
        if (userRequest.getRoles() == null || userRequest.getRoles().isEmpty()) {
            roles.add(roleRepository.findByName(RoleName.EMPLOYEE)
                    .orElseThrow(() -> new BadRequestException("Default role not found")));
        } else {
            for (RoleName roleName : userRequest.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new BadRequestException("Role not found: " + roleName));
                roles.add(role);
            }
        }
          newUser.setRoles(roles);

           User savedUser = userRepository.save(newUser);

        Set<RoleName> roleNames = new HashSet<>();
        for (Role role : savedUser.getRoles()) {
            roleNames.add(role.getName());
        }


        return UserResponse.builder()
                 .id(savedUser.getId())
                   .userID(savedUser.getUserID())
                   .userName(savedUser.getUserName())
                   .userEmail(savedUser.getUserEmail())
                   .passwordChangeDate(savedUser.getPasswordChangeDate())
                   .createdDate(savedUser.getCreatedDate())
                   .updatedDate(savedUser.getUpdatedDate())
                   .createdBy(savedUser.getCreatedBy())
                   .updatedBy(savedUser.getUpdatedBy())
                   .status(savedUser.getStatus())
                   .mobileNumber(savedUser.getMobileNumber())
                   .city(savedUser.getCity())
                   .country(savedUser.getCountry())
                   .state(savedUser.getState())
                   .cluster(savedUser.getCluster())
                   .roles(roleNames)
                   .build();
    }

    private String generateUserId() {
        String prefix = "USER";
        String lastUserId = userRepository.findLastUserId();

        int nextNumber = 1;
        if (lastUserId != null && lastUserId.startsWith(prefix)) {
            String numericPart = lastUserId.substring(prefix.length());
            nextNumber = Integer.parseInt(numericPart) + 1;
        }

        return String.format("%s%04d", prefix, nextNumber);
    }

    @Override
    public UserResponse getUserById(String userID) {

        if (userID == null || userID.trim().isEmpty()) {
            throw new BadRequestException(
                    "User ID must not be null or empty"
            );
        }

        User user = userRepository.findByUserID(userID.trim())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with ID : " + userID
                        ));

        Set<RoleName> roleNames = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .userID(user.getUserID())
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .passwordChangeDate(user.getPasswordChangeDate())
                .createdDate(user.getCreatedDate())
                .createdBy(user.getCreatedBy())
                .updatedDate(user.getUpdatedDate())
                .updatedBy(user.getUpdatedBy())
                .status(user.getStatus())
                .mobileNumber(user.getMobileNumber())
                .city(user.getCity())
                .state(user.getState())
                .country(user.getCountry())
                .cluster(user.getCluster())
                .roles(roleNames)
                .build();
    }

    @Override
    public void deleteAllUsers(boolean confirm) {

        if (!confirm) {
            throw new BadRequestException(
                    "Confirmation required to delete all users"
            );
        }

        long count = userRepository.count();

        if (count == 0) {
            throw new ResourceNotFoundException(
                    "No users found to delete"
            );
        }

        try {
            userRepository.deleteAll();

        } catch (Exception ex) {
            throw new UserBulkDeletionException(
                    "Failed to delete all users: " + ex.getMessage()
            );
        }
    }

    @Override
    public UserResponse deleteUserById(String userID) {

        if (userID == null || userID.trim().isEmpty()) {

            throw new BadRequestException(
                    "User ID cannot be empty"
            );
        }

        User user = userRepository.findByUserID(userID)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with ID : " + userID
                        ));


        if (user.getStatus() == Status.DELETED) {

            throw new UserDeletionException(
                    "User is already marked as deleted"
            );
        }

        UserResponse response = UserResponse.builder()
                .userID(user.getUserID())
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .status(user.getStatus())
                .mobileNumber(user.getMobileNumber())
                .createdDate(user.getCreatedDate())
                .updatedDate(user.getUpdatedDate())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .build();

        try {

            userRepository.delete(user);

        } catch (DataIntegrityViolationException ex) {

            throw new UserDeletionException(
                    "User cannot be deleted due to existing references"
            );
        }

        log.info("User deleted with email: {}", user.getUserEmail());

        return response;
    }

    @Override
    public UserResponse updateUser(String userID, UpdateUserRequest updateUserRequest) {

        if (updateUserRequest == null) {
            throw new BadRequestException(
                    "Update request must not be null"
            );
        }
        User user = userRepository.findByUserID(userID)
                .orElseThrow(()-> new UserNotFoundException("User not found with ID :"+ userID));

        if (user.getStatus() == Status.DELETED) {

            throw new UserUpdateException(
                    "Deleted users cannot be updated"
            );
        }

        if (updateUserRequest.getUserEmail() != null &&
                !updateUserRequest.getUserEmail()
                        .equalsIgnoreCase(user.getUserEmail())) {

            if (userRepository.existsByUserEmail(
                    updateUserRequest.getUserEmail())) {

                throw new UserUpdateException(
                        "Email already exists"
                );
            }

            user.setUserEmail(
                    updateUserRequest.getUserEmail().trim().toLowerCase()
            );
        }



        if (updateUserRequest.getUserName() != null &&
                !updateUserRequest.getUserName()
                        .equalsIgnoreCase(user.getUserName())) {

            if (userRepository.existsByUserName(
                    updateUserRequest.getUserName())) {

                throw new UserUpdateException(
                        "Username already exists"
                );
            }

            user.setUserName(
                    updateUserRequest.getUserName().trim()
            );
        }

        if (updateUserRequest.getMobileNumber() != null &&
                !updateUserRequest.getMobileNumber()
                        .equalsIgnoreCase(user.getMobileNumber())) {

            if (userRepository.existsByMobileNumber(
                    updateUserRequest.getMobileNumber())) {

                throw new UserUpdateException(
                        "Mobile number already exists"
                );
            }

            user.setMobileNumber(
                    updateUserRequest.getMobileNumber()
            );
        }

        if (updateUserRequest.getUserPassword() != null &&
                !updateUserRequest.getUserPassword().isBlank()) {

            user.setUserPassword(
                    updateUserRequest.getUserPassword()
            );

            user.setPasswordChangeDate(
                    LocalDateTime.now()
            );
        }

        if (updateUserRequest.getRoles() != null &&
                !updateUserRequest.getRoles().isEmpty()) {

            Set<Role> updatedRoles = new HashSet<>();

            for (RoleName roleName : updateUserRequest.getRoles()) {

                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Role not found : " + roleName
                                ));

                updatedRoles.add(role);
            }

            user.setRoles(updatedRoles);
        }


        user.setUpdatedDate(LocalDateTime.now());
        user.setUpdatedBy("SYSTEM");

       User savedUser =  userRepository.save(user);

       Set<RoleName>  roleNames = new HashSet<>();
       for (Role role : user.getRoles()){
           roleNames.add(role.getName());
       }


        return UserResponse.builder()
                .userID(savedUser.getUserID())
                .userName(savedUser.getUserName())
                .userEmail(savedUser.getUserEmail())
                .status(savedUser.getStatus())
                .mobileNumber(savedUser.getMobileNumber())
                .passwordChangeDate(savedUser.getPasswordChangeDate())
                .createdDate(savedUser.getCreatedDate())
                .createdBy(savedUser.getCreatedBy())
                .updatedDate(savedUser.getUpdatedDate())
                .updatedBy(savedUser.getUpdatedBy())
                .city(savedUser.getCity())
                .state(savedUser.getState())
                .country(savedUser.getCountry())
                .cluster(savedUser.getCluster())
                .roles(roleNames)
                .build();
    }

    @Override
    public UserResponse softDeleteUser(String userID) {

        if (userID == null || userID.trim().isEmpty()) {

            throw new BadRequestException(
                    "User ID cannot be empty"
            );
        }

        User user = userRepository.findByUserID(userID)
                .orElseThrow(()-> new UserNotFoundException("user not found with ID :"+ userID));

        if (user.getStatus() == Status.DELETED) {

            throw new UserSoftDeleteException(
                    "User is already deleted"
            );
        }
        user.setStatus(Status.DELETED);
       User savedUser =  userRepository.save(user);


        Set<RoleName> roleNames = new HashSet<>();
        for (Role role : user.getRoles()) {
            roleNames.add(role.getName());
        }


        return UserResponse.builder()
                .userID(savedUser.getUserID())
                .userName(savedUser.getUserName())
                .userEmail(savedUser.getUserEmail())
                .passwordChangeDate(savedUser.getPasswordChangeDate())
                .createdDate(savedUser.getCreatedDate())
                .createdBy(savedUser.getCreatedBy())
                .updatedDate(savedUser.getUpdatedDate())
                .updatedBy(savedUser.getUpdatedBy())
                .status(savedUser.getStatus())
                .city(savedUser.getCity())
                .state(savedUser.getState())
                .country(savedUser.getCountry())
                .cluster(savedUser.getCluster())
                .mobileNumber(savedUser.getMobileNumber())
                .roles(roleNames)
                .build();
    }

    @Override
    public UserResponse activateUser(String userID) {

        if (userID == null || userID.trim().isEmpty()) {

            throw new BadRequestException(
                    "User ID cannot be empty"
            );
        }
       User user =  userRepository.findByUserID(userID)
                .orElseThrow(()->  new UserNotFoundException("user not found with ID :"+ userID));

        if (user.getStatus() == Status.DELETED) {

            throw new UserActivationException(
                    "Deleted users cannot be activated"
            );
        }

        if (user.getStatus() == Status.ACTIVE) {

            throw new UserActivationException(
                    "User is already active"
            );
        }

        user.setStatus(Status.ACTIVE);
        User savedUser  = userRepository.save(user);

        Set<RoleName> roleNames = new HashSet<>();
        for (Role role : user.getRoles()){
            roleNames.add(role.getName());
        }


        return UserResponse.builder()
                .userID(savedUser.getUserID())
                .userName(savedUser.getUserName())
                .userEmail(savedUser.getUserEmail())
                .passwordChangeDate(savedUser.getPasswordChangeDate())
                .createdDate(savedUser.getCreatedDate())
                .createdBy(savedUser.getCreatedBy())
                .updatedDate(savedUser.getUpdatedDate())
                .updatedBy(savedUser.getUpdatedBy())
                .status(savedUser.getStatus())
                .city(savedUser.getCity())
                .state(savedUser.getState())
                .cluster(savedUser.getCluster())
                .city(savedUser.getCity())
                .roles(roleNames)
                .mobileNumber(savedUser.getMobileNumber())
                .build();

    }

    @Override
    public UserResponse deactivateUser(String userID) {

        if (userID == null || userID.trim().isEmpty()) {

            throw new BadRequestException(
                    "User ID cannot be empty"
            );
        }
        User user  = userRepository.findByUserID(userID)
                .orElseThrow(()-> new UserNotFoundException("User not found with ID :"+ userID));

        if (user.getStatus() == Status.DELETED) {

            throw new UserDeactivationException(
                    "Deleted users cannot be deactivated"
            );
        }

        if (user.getStatus() == Status.INACTIVE) {

            throw new UserDeactivationException(
                    "User is already inactive"
            );
        }

        user.setStatus(Status.INACTIVE);
        User savedUser = userRepository.save(user);

        Set<RoleName> roleNames =  new HashSet<>();
        for (Role role : user.getRoles()){
            roleNames.add(role.getName());
        }

        return UserResponse.builder()
                .userID(savedUser.getUserID())
                .userName(savedUser.getUserName())
                .userEmail(savedUser.getUserEmail())
                .passwordChangeDate(savedUser.getPasswordChangeDate())
                .createdDate(savedUser.getCreatedDate())
                .createdBy(savedUser.getCreatedBy())
                .updatedDate(savedUser.getUpdatedDate())
                .updatedBy(savedUser.getUpdatedBy())
                .status(savedUser.getStatus())
                .mobileNumber(savedUser.getMobileNumber())
                .city(savedUser.getCity())
                .state(savedUser.getState())
                .cluster(savedUser.getCluster())
                .country(savedUser.getCountry())
                .roles(roleNames)
                .build();
    }

    @Override
    public UserResponse restoreUser(String userID) {


        if (userID == null || userID.trim().isEmpty()) {
            throw new BadRequestException("User ID cannot be empty");
        }
        User user = userRepository.findByUserID(userID)
                .orElseThrow(()-> new UserNotFoundException("User not found with ID :"+ userID));
        if (user.getStatus() != Status.DELETED){
            throw new UserRestoreException("Only deleted users can restore users from the system");

        }
        user.setStatus(Status.ACTIVE);
       User savedUser =  userRepository.save(user);

       Set<RoleName> roleNames  = new HashSet<>();
       for (Role role : user.getRoles()){
           roleNames.add(role.getName());
       }

        return UserResponse.builder()
                .id(savedUser.getId())
                .userID(savedUser.getUserID())
                .userName(savedUser.getUserName())
                .userEmail(savedUser.getUserEmail())
                .passwordChangeDate(savedUser.getPasswordChangeDate())
                .createdDate(savedUser.getCreatedDate())
                .createdBy(savedUser.getCreatedBy())
                .updatedDate(savedUser.getUpdatedDate())
                .updatedBy(savedUser.getUpdatedBy())
                .status(savedUser.getStatus())
                .mobileNumber(savedUser.getMobileNumber())
                .city(savedUser.getCity())
                .state(savedUser.getState())
                .cluster(savedUser.getCluster())
                .country(savedUser.getCountry())
                .roles(roleNames)
                .build();
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(
            int page,
            int size,
            String sortBy,
            String direction,
            Status status,
            RoleName role,
            String name) {

        if (page < 0) {
            throw new BadRequestException("Page index cannot be negative");
        }

        if (size <= 0) {
            throw new BadRequestException("Page size must be greater than 0");
        }

        if (size > 100) {
            throw new BadRequestException("Page size cannot exceed 100");
        }

        if (sortBy == null || sortBy.trim().isEmpty()) {
            throw new BadRequestException("SortBy field is required");
        }

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(sortDirection, sortBy)
                .and(Sort.by(Sort.Direction.ASC, "userID"));

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> usersPage = userRepository.findUsersWithFilters(
                status, role, name, pageable
        );

        List<UserResponse> userResponses = usersPage.getContent()
                .stream()
                .map(user -> {
                    Set<RoleName> roleNames = user.getRoles()
                            .stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet());

                    return UserResponse.builder()
                            .userID(user.getUserID())
                            .userName(user.getUserName())
                            .userEmail(user.getUserEmail())
                            .passwordChangeDate(user.getPasswordChangeDate())
                            .createdDate(user.getCreatedDate())
                            .createdBy(user.getCreatedBy())
                            .updatedDate(user.getUpdatedDate())
                            .updatedBy(user.getUpdatedBy())
                            .status(user.getStatus())
                            .state(user.getState())
                            .city(user.getCity())
                            .cluster(user.getCluster())
                            .country(user.getCountry())
                            .mobileNumber(user.getMobileNumber())
                            .roles(roleNames)
                            .build();
                })
                .toList();

        return PageResponse.<UserResponse>builder()
                .content(userResponses)
                .page(usersPage.getNumber())
                .size(usersPage.getSize())
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .numberOfElements(usersPage.getNumberOfElements())
                .first(usersPage.isFirst())
                .last(usersPage.isLast())
                .hasNext(usersPage.hasNext())
                .hasPrevious(usersPage.hasPrevious())
                .sortBy(sortBy)
                .direction(direction)
                .build();
    }
}
