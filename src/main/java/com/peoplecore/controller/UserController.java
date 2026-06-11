package com.peoplecore.controller;
import com.peoplecore.dto.request.UpdateUserRequest;
import com.peoplecore.dto.request.UserRequest;
import com.peoplecore.dto.response.PageResponse;
import com.peoplecore.dto.response.UserResponse;
import com.peoplecore.enums.RoleName;
import com.peoplecore.enums.Status;
import com.peoplecore.service.UserService;
import com.peoplecore.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "User Management",
        description = """
                Comprehensive APIs for managing system users across the organization.
                
                Features include:
                • User registration and profile management
                • User retrieval with pagination, sorting, and filtering
                • Account activation and deactivation
                • Soft delete and permanent deletion
                • Restoration of deleted accounts
                • Role-based user administration
                • Lifecycle management and auditing
                
                These APIs are designed for enterprise-grade user administration,
                security, and access control.
                """
)
@RestController
@RequestMapping("/api/v1/user")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }


    @Operation(summary = "Create a new user",
            description = "Creates a new user in the system")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> registerUsers(@Valid @RequestBody UserRequest userRequest, HttpServletRequest httpServletRequest){
        UserResponse userResponse =   userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(),"User created successful",httpServletRequest.getRequestURI(),userResponse));
    }


    @Operation(
            summary = "Retrieve all users",
            description = "Fetches a paginated list of users with optional filtering by status, role, and name. Supports sorting and pagination."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or filter parameters"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")})
    @GetMapping("/fetche-all-users")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be 0 or greater")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be greater than 0")
            @Max(value = 100, message = "Size cannot exceed 100")
            int size,

            @RequestParam(defaultValue = "createdDate")
            @NotBlank(message = "SortBy must not be blank")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            @Pattern(regexp = "asc|desc", message = "Direction must be asc or desc")
            String direction,

            @RequestParam(required = false)
            Status status,

            @RequestParam(required = false)
            RoleName role,

            @RequestParam(required = false)
            String name,

            HttpServletRequest httpServletRequest
    ) {

        PageResponse<UserResponse> usersResponse =
                userService.getAllUsers(page, size, sortBy, direction, status, role, name);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Users successfully fetched",
                        httpServletRequest.getRequestURI(),
                        usersResponse
                ));
    }


    /*This is only for testing purpose*/
    @Operation(
            summary = "Delete all users",
            description = "Deletes all users from the system. This endpoint is intended strictly for testing or development environments."
    )

    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "All users deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")})
    @DeleteMapping("/delete-all")
    public ResponseEntity<ApiResponse<Void>> deleteAllUsers( @RequestParam(defaultValue = "false")
                                                                 boolean confirm,HttpServletRequest httpServletRequest) {
        userService.deleteAllUsers(confirm);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), httpServletRequest.getRequestURI(),"All users deleted successfully", null));
    }


    @Operation(
            summary = "Get user by ID",
            description = "Retrieves complete details of a specific user using the unique user ID."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found")})
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByID(@PathVariable   @NotBlank(message = "User ID is required")
                                                                     @Size(min = 5, max = 30,
                                                                             message = "Invalid user ID format") String userId, HttpServletRequest httpServletRequest){
        UserResponse userResponse = userService.getUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "User retrieved successfully", httpServletRequest.getRequestURI(), userResponse));
    }


    @Operation(
            summary = "Update user",
            description = "Updates an existing user's profile, role, status, and other editable details."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data")})
    @PostMapping("/{id}/UpdateUser")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable("id") @NotBlank(message = "User ID is required")
                                                                    @Size(min = 5, max = 30,
                                                                            message = "Invalid user ID format") String userId ,@Valid @RequestBody UpdateUserRequest updateUserRequest, HttpServletRequest httpServletRequest){
        UserResponse userResponse = userService.updateUser(userId,updateUserRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "User updated successful",httpServletRequest.getRequestURI(),userResponse));
    }


    @Operation(
            summary = "Soft delete user",
            description = "Marks a user as deleted without permanently removing the record from the database.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User soft deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found")})
    @DeleteMapping("/{id}/softDeleteUser")
    public ResponseEntity<ApiResponse<UserResponse>> softDeleteUser(@PathVariable("id") @NotBlank(message = "User ID is required")
                                                                        @Size(min = 5, max = 30,
                                                                                message = "Invalid user ID format")String userId, HttpServletRequest  httpServletRequest){
        UserResponse userResponse = userService.softDeleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "User deleted successful", httpServletRequest.getRequestURI(), userResponse));
    }

    @Operation(
            summary = "Permanently delete user",
            description = "Permanently removes a user from the system. This operation cannot be undone.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User permanently deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found")})
    @DeleteMapping("/{id}/deleteUser")
    public ResponseEntity<ApiResponse<UserResponse>> deleteUser(@PathVariable("id") @NotBlank(message = "User ID is required")
                                                                    @Size(min = 5, max = 30,
                                                                            message = "Invalid user ID format")String userId, HttpServletRequest httpServletRequest){
        UserResponse userResponse = userService.deleteUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "User deleted successful", httpServletRequest.getRequestURI(), userResponse));
    }


    @Operation(
            summary = "Activate user",
            description = "Activates a user account, allowing login and system access.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User activated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found")
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(@PathVariable("id")@NotBlank(message = "User ID is required")
                                                                      @Size(min = 5, max = 30,
                                                                              message = "Invalid user ID format") String userId, HttpServletRequest httpServletRequest){
        UserResponse userResponse = userService.activateUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "user Activated successful", httpServletRequest.getRequestURI(), userResponse));
    }


    @Operation(
            summary = "Deactivate user",
            description = "Deactivates a user account, preventing login and access to the system.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User deactivated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found")})
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(@PathVariable("id")  @NotBlank(message = "User ID is required")
                                                                        @Size(min = 5, max = 30,
                                                                                message = "Invalid user ID format") String userId, HttpServletRequest httpServletRequest){
        UserResponse userResponse = userService.deactivateUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "User Deactivated successful", httpServletRequest.getRequestURI(), userResponse));
    }


    @Operation(
            summary = "Restore deleted user",
            description = "Restores a previously soft-deleted user back to active status.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User restored successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found")})
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<UserResponse>> restoreUser(@PathVariable("id")
                                                                     @NotBlank(message = "User ID is required")
                                                                     @Size(min = 5, max = 30,
                                                                             message = "Invalid user ID format")String userId, HttpServletRequest httpServletRequest){
        UserResponse userResponse = userService.restoreUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "User Restored successful", httpServletRequest.getRequestURI(), userResponse));
    }
}

