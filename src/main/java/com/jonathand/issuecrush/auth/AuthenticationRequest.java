package com.jonathand.issuecrush.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "You must provide a valid email")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

}
