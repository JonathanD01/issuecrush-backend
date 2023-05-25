package com.jonathand.issuecrush.auth;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.config.JwtService;
import com.jonathand.issuecrush.user.User;
import com.jonathand.issuecrush.user.UserDTOMapper;
import com.jonathand.issuecrush.user.UserRepository;
import com.jonathand.issuecrush.user.UserUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private final Faker faker = new Faker();

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDTOMapper userDTOMapper;

    @Mock
    private UserUtil userUtil;

    @InjectMocks
    private AuthenticationService underTest;

    @Test
    void canRegister() {
        // given
        RegisterRequest registerRequest = new RegisterRequest(faker.name()
            .firstName(), faker.name()
            .lastName(), faker.internet()
            .emailAddress(), faker.internet()
            .password());

        // when
        underTest.register(registerRequest);

        // then
        verify(userRepository).save(any());
    }

    @Test
    void canAuthenticate() {
        // given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(faker.name()
            .username(), faker.internet()
            .password());

        UsernamePasswordAuthenticationToken authenticationToken = mock(UsernamePasswordAuthenticationToken.class);

        when(authenticationManager.authenticate(any())).thenReturn(authenticationToken);

        when(jwtService.generateToken(any())).thenReturn("jwtToken");

        // when
        AuthenticationResponse authenticationResponse = underTest.authenticate(authenticationRequest);

        // then
        assertThat(authenticationResponse).isNotNull();
        assertThat(authenticationResponse.getToken()).isEqualTo("jwtToken");

        verify(authenticationManager).authenticate(any());
    }

    @Test
    void canGetUser() {
        // given
        String email = faker.internet()
            .emailAddress();

        User user = mock(User.class);

        when(userUtil.getUserByEmail(email)).thenReturn(user);

        // when
        underTest.getUserFromEmail(email);

        // then
        verify(userDTOMapper).apply(user);
    }

}
