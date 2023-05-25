package com.jonathand.issuecrush.auth;

import com.jonathand.issuecrush.config.JwtService;
import com.jonathand.issuecrush.user.User;
import com.jonathand.issuecrush.user.UserDTO;
import com.jonathand.issuecrush.user.UserDTOMapper;
import com.jonathand.issuecrush.user.UserRepository;
import com.jonathand.issuecrush.user.UserRole;
import com.jonathand.issuecrush.user.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final UserDTOMapper userDTOMapper;

    private final UserUtil userUtil;

    /**
     * Retrieves user details.
     *
     * @param email the email of the user
     * @return the API response with the user details
     * @throws UsernameNotFoundException if the user with the given username is not found
     */
    public UserDTO getUserFromEmail(String email) {
        // Find the user by email
        User user = userUtil.getUserByEmail(email);

        // Build and return the API response with the user details
        return userDTOMapper.apply(user);
    }

    /**
     * Registers a new user.
     *
     * @param request the register request containing user details
     * @return the authentication response with the generated token
     * @throws EmailAlreadyTakenException if the email is already associated with an existing user
     */
    public AuthenticationResponse register(RegisterRequest request) {
        // Validate email does not exist
        userUtil.validateEmailDoesNotExist(request.getEmail());

        // Create a new user entity
        User user =
            User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userRole(UserRole.USER)
                .build();

        // Save the user entity in the repository
        userRepository.save(user);

        // Generate JWT token for the user
        String jwtToken = jwtService.generateToken(user);

        // Build and return the authentication response
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
    }

    /**
     * Authenticates a user.
     *
     * @param request the authentication request containing user credentials
     * @return the authentication response with the generated token
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authentication =
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Get the authenticated user
        User user = (User) authentication.getPrincipal();

        // Generate JWT token for the user
        String jwtToken = jwtService.generateToken(user);

        // Build and return the authentication response
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
    }

}
