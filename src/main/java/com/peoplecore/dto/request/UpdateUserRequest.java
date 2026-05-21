package com.peoplecore.dto.request;

import com.peoplecore.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateUserRequest {

    @Size(min = 3, max = 50,
            message = "Username must be between 3 and 50 characters")
    private String userName;

    @Email(message = "Invalid email format")
    private String userEmail;

    @Size(min = 8, max = 100,
            message = "Password must be between 8 and 100 characters")
    private String userPassword;

    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid mobile number"
    )
    private String mobileNumber;

    private Set<RoleName> roles;
}
