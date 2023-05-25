package com.jonathand.issuecrush.organization;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.List;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.UserRegisterExtractor;
import com.jonathand.issuecrush.auth.AuthenticationResponse;
import com.jonathand.issuecrush.organization.user.OrganizationUserDTO;
import com.jonathand.issuecrush.organization.user.OrganizationUserRepository;
import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "PT30S")
class OrganizationIntegrationTest {

    private static final String ORGANIZATION_PATH = "/api/v1/organizations";

    private static final String ORGANIZATION_USER_PATH = "/api/v1/organization-users";

    private static final String AUTHENTICATION_PATH = "/api/v1/auth";

    private final Faker faker = new Faker();

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationUserRepository organizationUserRepository;

    @Test
    void canCreateAnOrganization() {
        String email = faker.internet()
            .emailAddress();
        String password = faker.internet()
            .password();

        // get jwt token
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

        // use jwt token to create an organization
        String organizationName = faker.company()
            .name();

        OrganizationNewRequest organizationNewRequest = new OrganizationNewRequest(organizationName);

        EntityExchangeResult<APIResponse<OrganizationDTO>> organizationResult = webTestClient.post()
            .uri(ORGANIZATION_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(organizationNewRequest), OrganizationNewRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<OrganizationDTO>>() {
            })
            .returnResult();

        APIResponse<OrganizationDTO> response = organizationResult.getResponseBody();

        assert response != null;
        assertThat(response.getResponseType()).isEqualTo(APIResponseType.SUCCESS);
        assertThat(response.getErrors()).isNull();
        assertThat(response.getResult()
            .name()).isEqualTo(organizationName);
        assertThat(response.getResult()
            .createdAt()).isNotNull();
        assertThat(response.getResult()
            .updatedAt()).isNotNull();
        assertThat(response.getResult()
            .owner()
            .firstName()).isNotNull();
        assertThat(response.getResult()
            .owner()
            .lastName()).isNotNull();
        assertThat(response.getResult()
            .owner()
            .email()).isEqualTo(email);
    }

    @Test
    void canUpdateAnOrganization() {
        String email = faker.internet()
            .emailAddress();
        String password = faker.internet()
            .password();

        // get jwt token
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

        // use jwt token to create an organization
        String organizationName = faker.company()
            .name();

        OrganizationNewRequest organizationNewRequest = new OrganizationNewRequest(organizationName);

        EntityExchangeResult<APIResponse<OrganizationDTO>> newOrganizationResult = webTestClient.post()
            .uri(ORGANIZATION_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(organizationNewRequest), OrganizationNewRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<OrganizationDTO>>() {
            })
            .returnResult();

        APIResponse<OrganizationDTO> createdOrganizationResponse = newOrganizationResult.getResponseBody();

        assert createdOrganizationResponse != null;

        String newOrganizationName = faker.company()
            .name();

        OrganizationUpdateRequest updateRequest = new OrganizationUpdateRequest(newOrganizationName);

        Long organizationId = createdOrganizationResponse.getResult()
            .id();

        EntityExchangeResult<APIResponse<OrganizationDTO>> updatedOrganizationResult = webTestClient.put()
            .uri(ORGANIZATION_PATH + "/{organizationId}", organizationId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(updateRequest), OrganizationUpdateRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<OrganizationDTO>>() {
            })
            .returnResult();

        APIResponse<OrganizationDTO> updatedOrganizationResponse = updatedOrganizationResult.getResponseBody();

        assert updatedOrganizationResponse != null;

        OrganizationDTO createdOrganization = createdOrganizationResponse.getResult();

        OrganizationDTO updatedOrganization = updatedOrganizationResponse.getResult();

        assert updatedOrganization != null;

        assertThat(updatedOrganizationResponse.getResponseType()).isEqualTo(APIResponseType.SUCCESS);
        assertThat(updatedOrganizationResponse.getErrors()).isNull();

        assertThat(updatedOrganization.id()).isEqualTo(createdOrganization.id());
        assertThat(updatedOrganization.name()).isNotEqualTo(organizationName);
        assertThat(updatedOrganization.name()).isNotEqualTo(createdOrganization.name());
        assertThat(updatedOrganization.createdAt()).isEqualTo(createdOrganization.createdAt());
        assertThat(updatedOrganization.updatedAt()).isNotEqualTo(createdOrganization.updatedAt());
    }

    @Test
    void canDeleteOrganization() {
        String email = faker.internet()
            .emailAddress();
        String password = faker.internet()
            .password();

        // get jwt token
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

        // use jwt token to create an organization
        String organizationName = faker.company()
            .name();

        OrganizationNewRequest organizationNewRequest = new OrganizationNewRequest(organizationName);

        EntityExchangeResult<APIResponse<OrganizationDTO>> newOrganizationResult = webTestClient.post()
            .uri(ORGANIZATION_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(organizationNewRequest), OrganizationNewRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<OrganizationDTO>>() {
            })
            .returnResult();

        APIResponse<OrganizationDTO> createdOrganizationResponse = newOrganizationResult.getResponseBody();

        assert createdOrganizationResponse != null;

        Long organizationId = createdOrganizationResponse.getResult()
            .id();

        EntityExchangeResult<APIResponse<Long>> deleteOrganizationResult = webTestClient.delete()
            .uri(ORGANIZATION_PATH + "/{organizationId}", organizationId)
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<Long>>() {
            })
            .returnResult();

        APIResponse<Long> deletedOrganization = deleteOrganizationResult.getResponseBody();

        assert deletedOrganization != null;

        OrganizationDTO createdOrganization = createdOrganizationResponse.getResult();

        Long createdOrganizationId = createdOrganization.id();

        // Make sure organization is deleted
        webTestClient.get()
            .uri(ORGANIZATION_PATH + "/{organizationId}", createdOrganizationId)
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .jsonPath("$.response")
            .isEqualTo("FAILED")
            .jsonPath("$.errors[0].message")
            .isEqualTo("Organization with id " + createdOrganizationId + " does not exist");

        Long deletedOrganizationId = deletedOrganization.getResult();

        assertThat(deletedOrganizationId).isEqualTo(createdOrganization.id());
    }

    @Test
    void canGetOrganization() {
        String email = faker.internet()
            .emailAddress();
        String password = faker.internet()
            .password();

        // get jwt token
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

        // use jwt token to create an organization
        String organizationName = faker.company()
            .name();

        OrganizationNewRequest organizationNewRequest = new OrganizationNewRequest(organizationName);

        EntityExchangeResult<APIResponse<OrganizationDTO>> newOrganizationResult = webTestClient.post()
            .uri(ORGANIZATION_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(organizationNewRequest), OrganizationNewRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<OrganizationDTO>>() {
            })
            .returnResult();

        APIResponse<OrganizationDTO> createdOrganizationResponse = newOrganizationResult.getResponseBody();

        assert createdOrganizationResponse != null;
        OrganizationDTO createdOrganization = createdOrganizationResponse.getResult();

        EntityExchangeResult<APIResponse<OrganizationDTO>> getOrganizationResult = webTestClient.get()
            .uri(ORGANIZATION_PATH + "/{organizationId}", createdOrganization.id())
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<OrganizationDTO>>() {
            })
            .returnResult();

        APIResponse<OrganizationDTO> gotOrganizationResponse = getOrganizationResult.getResponseBody();

        assert gotOrganizationResponse != null;
        OrganizationDTO gotOrganization = gotOrganizationResponse.getResult();

        assertThat(gotOrganizationResponse.getResponseType()).isEqualTo(APIResponseType.SUCCESS);
        assertThat(gotOrganizationResponse.getErrors()).isNull();

        assertThat(gotOrganization.id()).isEqualTo(createdOrganization.id());
        assertThat(gotOrganization.name()).isEqualTo(createdOrganization.name());
        assertThat(gotOrganization.createdAt()).isEqualTo(createdOrganization.createdAt());
    }

    @Test
    void canGetOrganizationsUserIsIn() {
        String email = faker.internet()
            .emailAddress();
        String password = faker.internet()
            .password();

        // get jwt token
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

        int organizationsToCreate = 5;
        int returnSize = 2;
        for (int i = 0; i < organizationsToCreate; i++) {
            // use jwt token to create an organization
            String organizationName = faker.company()
                .name();

            OrganizationNewRequest organizationNewRequest = new OrganizationNewRequest(organizationName);

            webTestClient.post()
                .uri(ORGANIZATION_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(organizationNewRequest), OrganizationNewRequest.class)
                .headers(header -> header.setBearerAuth(jwtToken))
                .exchange()
                .expectStatus()
                .isOk();
        }

        EntityExchangeResult<APIResponse<List<OrganizationDTO>>> organizationResult = webTestClient.get()
            .uri(ORGANIZATION_PATH + "?size={size}", returnSize)
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<List<OrganizationDTO>>>() {
            })
            .returnResult();

        APIResponse<List<OrganizationDTO>> organizationResponse = organizationResult.getResponseBody();

        assert organizationResponse != null;

        assertThat(organizationResponse.getResponseType()).isEqualTo(APIResponseType.SUCCESS);
        assertThat(organizationResponse.getErrors()).isNull();
        assertThat(organizationResponse.getResult()
            .size()).isEqualTo(returnSize);
    }

    @Test
    @Transactional
    void canAddUserToOrganization() {
        // given
        String emailForOrganizationOwner = faker.internet()
            .emailAddress();
        String emailForUserToBeAdded = faker.internet()
            .emailAddress();

        // register the user to be added
        UserRegisterExtractor.builder()
            .webTestClient(webTestClient)
            .authenticationPath(AUTHENTICATION_PATH)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .email(emailForUserToBeAdded)
            .password(faker.internet()
                .password())
            .build()
            .get();

        // get jwt token from owner user
        EntityExchangeResult<AuthenticationResponse> authResult = UserRegisterExtractor.builder()
            .webTestClient(webTestClient)
            .authenticationPath(AUTHENTICATION_PATH)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .email(emailForOrganizationOwner)
            .password(faker.internet()
                .password())
            .build()
            .get();

        assert authResult.getResponseBody() != null;

        String jwtToken = authResult.getResponseBody()
            .getToken();

        // use jwt token to create organization
        String organizationName = faker.company()
            .name();

        OrganizationNewRequest organizationNewRequest = new OrganizationNewRequest(organizationName);

        EntityExchangeResult<APIResponse<OrganizationDTO>> organizationResult = webTestClient.post()
            .uri(ORGANIZATION_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(organizationNewRequest), OrganizationNewRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<OrganizationDTO>>() {
            })
            .returnResult();

        assert organizationResult.getResponseBody() != null;

        Long organizationId = organizationResult.getResponseBody()
            .getResult()
            .id();

        // Get the (owner) organization user
        EntityExchangeResult<APIResponse<OrganizationUserDTO>> organizationUserResult = webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path(ORGANIZATION_USER_PATH)
                .queryParam("organizationId", organizationId)
                .queryParam("email", emailForOrganizationOwner)
                .build())
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<OrganizationUserDTO>>() {
            })
            .returnResult();

        APIResponse<OrganizationUserDTO> userDTOResponse = organizationUserResult.getResponseBody();
        assert userDTOResponse != null;

        OrganizationUserDTO organizationUserDTO = userDTOResponse.getResult();
        Long organizationUserId = organizationUserDTO.id();

        // when

        // add another organization user
        OrganizationAddUserRequest organizationAddUserRequest = new OrganizationAddUserRequest(
            organizationUserId, emailForUserToBeAdded);

        webTestClient.post()
            .uri(ORGANIZATION_PATH + "/{organizationId}/users", organizationId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(organizationAddUserRequest), OrganizationAddUserRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk();

        // then
        Organization organization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new OrganizationNotFoundException(organizationId));

        assertThat(organization.getUsers()
            .size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @Transactional
    void canRemoveUserFromOrganization() {
        // given
        String emailForOrganizationOwner = faker.internet()
            .emailAddress();
        String emailForUserToBeRemoved = faker.internet()
            .emailAddress();

        // register the user to be removed
        UserRegisterExtractor.builder()
            .webTestClient(webTestClient)
            .authenticationPath(AUTHENTICATION_PATH)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .email(emailForUserToBeRemoved)
            .password(faker.internet()
                .password())
            .build()
            .get();

        // get jwt token
        EntityExchangeResult<AuthenticationResponse> authResult = UserRegisterExtractor.builder()
            .webTestClient(webTestClient)
            .authenticationPath(AUTHENTICATION_PATH)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .email(emailForOrganizationOwner)
            .password(faker.internet()
                .password())
            .build()
            .get();

        assert authResult.getResponseBody() != null;

        String jwtToken = authResult.getResponseBody()
            .getToken();

        // use jwt token to create organization
        String organizationName = faker.company()
            .name();

        OrganizationNewRequest organizationNewRequest = new OrganizationNewRequest(organizationName);

        EntityExchangeResult<APIResponse<OrganizationDTO>> organizationResult = webTestClient.post()
            .uri(ORGANIZATION_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(organizationNewRequest), OrganizationNewRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<OrganizationDTO>>() {
            })
            .returnResult();

        assert organizationResult.getResponseBody() != null;

        Long organizationId = organizationResult.getResponseBody()
            .getResult()
            .id();

        // Get the (owner) organization user
        EntityExchangeResult<APIResponse<OrganizationUserDTO>> organizationUserResult = webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path(ORGANIZATION_USER_PATH)
                .queryParam("organizationId", organizationId)
                .queryParam("email", emailForOrganizationOwner)
                .build())
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<OrganizationUserDTO>>() {
            })
            .returnResult();

        APIResponse<OrganizationUserDTO> userDTOResponse = organizationUserResult.getResponseBody();
        assert userDTOResponse != null;

        OrganizationUserDTO organizationUserDTO = userDTOResponse.getResult();
        Long organizationUserId = organizationUserDTO.id();

        // add organization user to be removed to organization
        OrganizationAddUserRequest organizationAddUserRequest = new OrganizationAddUserRequest(
            organizationUserId, emailForUserToBeRemoved);

        EntityExchangeResult<APIResponse<OrganizationUserDTO>> organizationUserExchange = webTestClient.post()
            .uri(ORGANIZATION_PATH + "/{organizationId}/users", organizationId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(organizationAddUserRequest), OrganizationAddUserRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<OrganizationUserDTO>>() {
            })
            .returnResult();

        APIResponse<OrganizationUserDTO> organizationUserResponse2 = organizationUserExchange.getResponseBody();
        assert organizationUserResponse2 != null;
        OrganizationUserDTO organizationUserDTO2 = organizationUserResponse2.getResult();

        // when
        EntityExchangeResult<APIResponse<Long>> deletedResultId = webTestClient.delete()
            .uri(ORGANIZATION_PATH + "/{organizationId}/users" + "/{organizationUserId}", organizationId,
                organizationUserDTO2.id())
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<Long>>() {
            })
            .returnResult();

        // then
        Organization organization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new OrganizationNotFoundException(organizationId));

        assert deletedResultId.getResponseBody() != null;

        Long deletedOrganizationUserId = deletedResultId.getResponseBody()
            .getResult();

        assertThat(deletedOrganizationUserId).isNotEqualTo(0L);
        assertThat(organization.getUsers()
            .size()).isEqualTo(1);

        assertFalse(organizationUserRepository.existsById(deletedOrganizationUserId));
    }

}
