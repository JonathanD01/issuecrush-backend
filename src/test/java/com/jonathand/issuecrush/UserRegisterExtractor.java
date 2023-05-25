package com.jonathand.issuecrush;

import com.jonathand.issuecrush.auth.AuthenticationResponse;
import com.jonathand.issuecrush.auth.RegisterRequest;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@Builder
@RequiredArgsConstructor
public class UserRegisterExtractor {

    private final WebTestClient webTestClient;

    private final String authenticationPath;

    private final String firstName;

    private final String lastName;

    private final String email;

    private final String password;

    public EntityExchangeResult<AuthenticationResponse> get() {
        // Create user & get jwt token
        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, email, password);

        return webTestClient.post()
            .uri(authenticationPath + "/register")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(registerRequest), RegisterRequest.class)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
            })
            .returnResult();
    }

    public void exchange() {
        // Create user & get jwt token
        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, email, password);

        webTestClient.post()
            .uri(authenticationPath + "/register")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(registerRequest), RegisterRequest.class)
            .exchange();
    }

}
