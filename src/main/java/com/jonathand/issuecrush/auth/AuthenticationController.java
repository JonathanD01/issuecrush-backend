package com.jonathand.issuecrush.auth;

import java.security.Principal;

import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseUtil;
import com.jonathand.issuecrush.user.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final APIResponseUtil apiResponseUtil;

    /**
     * Retrieves the user information for the authenticated user.
     *
     * @param principal the principal object representing the authenticated user
     * @return ResponseEntity containing the API response with the user information
     */
    @GetMapping
    public ResponseEntity<APIResponse<UserDTO>> getUser(Principal principal) {
        UserDTO userDTO = authenticationService.getUserFromEmail(principal.getName());
        APIResponse<UserDTO> response = apiResponseUtil.buildSuccessResponse(userDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Registers a new user.
     *
     * @param request The RegisterRequest object containing the user registration details.
     * @return The ResponseEntity containing the AuthenticationResponse with the registration result.
     */
    @PostMapping("register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param request The AuthenticationRequest object containing the user authentication credentials.
     * @return The ResponseEntity containing the AuthenticationResponse with the authentication result.
     */
    @PostMapping("authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

}
