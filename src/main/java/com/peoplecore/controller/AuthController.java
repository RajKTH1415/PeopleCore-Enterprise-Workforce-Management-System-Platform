package com.peoplecore.controller;

import com.peoplecore.dto.request.UserRequest;
import com.peoplecore.dto.response.UserResponse;
import com.peoplecore.service.UserService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> registerUsers(@Valid @RequestBody UserRequest userRequest, HttpServletRequest httpServletRequest){
        UserResponse userResponse =   userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(),"User created successful",httpServletRequest.getRequestURI(),userResponse));
    }
}
