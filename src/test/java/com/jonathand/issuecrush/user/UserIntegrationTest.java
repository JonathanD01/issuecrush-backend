package com.jonathand.issuecrush.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.UserRegisterExtractor;
import com.jonathand.issuecrush.auth.AuthenticationResponse;
import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserIntegrationTest {

    private static final String AUTHENTICATION_PATH = "/api/v1/auth";

    private final Faker faker = new Faker();

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void canGetUser() {
        // given
        String email = faker.internet()
            .emailAddress();
        String password = faker.internet()
            .password();

        EntityExchangeResult<AuthenticationResponse> authResult = UserRegisterExtractor.builder()
            .webTestClient(webTestClient)
            .authenticationPath(AUTHENTICATION_PATH)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .email(email)
            .password(password)
            .build()
            .get();

        assert authResult.getResponseBody() != null;

        String jwtToken = authResult.getResponseBody()
            .getToken();

        // when
        EntityExchangeResult<APIResponse<UserDTO>> userResult = webTestClient.method(HttpMethod.GET)
            .uri(AUTHENTICATION_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<UserDTO>>() {
            })
            .returnResult();

        assert userResult.getResponseBody() != null;

        APIResponse<UserDTO> userDTO = userResult.getResponseBody();

        assertThat(userDTO.getResponseType()).isEqualTo(APIResponseType.SUCCESS);
        assertThat(userDTO.getResult()
            .firstName()).isNotNull();
        assertThat(userDTO.getResult()
            .lastName()).isNotNull();
        assertThat(userDTO.getResult()
            .email()).isEqualTo(email);
        assertThat(userDTO.getResult()
            .role()).isEqualTo("USER");
        assertTrue(userDTO.getResult()
            .enabled());
    }

}