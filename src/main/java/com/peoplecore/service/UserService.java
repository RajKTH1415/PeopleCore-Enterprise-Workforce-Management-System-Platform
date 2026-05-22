package com.peoplecore.service;
import com.peoplecore.dto.request.UpdateUserRequest;
import com.peoplecore.dto.request.UserRequest;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.dto.response.UserResponse;
import com.peoplecore.enums.RoleName;
import com.peoplecore.enums.Status;

public interface UserService {

    UserResponse createUser(UserRequest userRequest);

    UserResponse getUserById(String userID);

    void deleteAllUsers(boolean confirm);

    UserResponse deleteUserById(String userID);

    UserResponse updateUser(String userID , UpdateUserRequest updateUserRequest);

    UserResponse softDeleteUser(String userID);

    UserResponse activateUser(String userID);

    UserResponse deactivateUser(String userID);

    UserResponse restoreUser(String userID);

    PageResponse<UserResponse> getAllUsers(int page , int size, String sortBy, String direction , Status status , RoleName roleName , String name);


}
