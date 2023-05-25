package com.jonathand.issuecrush.auth;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.UserRegisterExtractor;
import com.jonathand.issuecrush.config.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class AuthenticationIntegrationTest {

    private static final String AUTHENTICATION_PATH = "/api/v1/auth";

    private final Faker faker = new Faker();

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtService jwtService;

    @Test
    void canRegister() {
        // given
        String email = faker.internet()
            .emailAddress();

        // when
        EntityExchangeResult<AuthenticationResponse> authResult = UserRegisterExtractor.builder()
            .webTestClient(webTestClient)
            .authenticationPath(AUTHENTICATION_PATH)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .email(email)
            .password(faker.internet()
                .password())
            .build()
            .get();

        assert authResult.getResponseBody() != null;

        String jwtToken = authResult.getResponseBody()
            .getToken();

        assertThat(jwtService.extractUsername(jwtToken)).isEqualTo(email);
        assertTrue(jwtService.isTokenValid(jwtToken, email));
    }

    @Test
    void canRegisterAndLogin() {
        // given
        String email = faker.internet()
            .emailAddress();
        String password = faker.internet()
            .password();

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, password);

        // Register user
        UserRegisterExtractor.builder()
            .webTestClient(webTestClient)
            .authenticationPath(AUTHENTICATION_PATH)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .email(email)
            .password(password)
            .build()
            .exchange();

        // when
        EntityExchangeResult<AuthenticationResponse> authResult = webTestClient.post()
            .uri(AUTHENTICATION_PATH + "/authenticate")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
            })
            .returnResult();

        assert authResult.getResponseBody() != null;

        String jwtToken = authResult.getResponseBody()
            .getToken();

        assertThat(jwtService.extractUsername(jwtToken)).isEqualTo(email);
        assertTrue(jwtService.isTokenValid(jwtToken, email));
    }

}