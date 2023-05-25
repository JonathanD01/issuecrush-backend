package com.jonathand.issuecrush.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.List;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.UserRegisterExtractor;
import com.jonathand.issuecrush.auth.AuthenticationResponse;
import com.jonathand.issuecrush.organization.OrganizationDTO;
import com.jonathand.issuecrush.organization.OrganizationNewRequest;
import com.jonathand.issuecrush.organization.user.OrganizationUserDTO;
import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseType;
import com.jonathand.issuecrush.ticket.property.TicketPriority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class TicketIntegrationTest {

    private static final String TICKET_PATH = "/api/v1/tickets";

    private static final String ORGANIZATION_PATH = "/api/v1/organizations";

    private static final String ORGANIZATION_USER_PATH = "/api/v1/organization-users";

    private static final String AUTHENTICATION_PATH = "/api/v1/auth";

    private final Faker faker = new Faker();

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void canGetAllTicketsForOrganizationWithNoContentResponse() {
        // given
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

        OrganizationDTO organizationDTO = response.getResult();
        Long organizationId = organizationDTO.id();

        // when
        // then
        webTestClient.method(HttpMethod.GET)
            .uri(TICKET_PATH + "/organization/{organizationId}", organizationId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isNoContent();
    }

    @Test
    void canGetAllTicketsForOrganization() {
        // given
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

        OrganizationDTO organizationDTO = response.getResult();
        Long organizationId = organizationDTO.id();

        int ticketsToCreate = faker.random()
            .nextInt(1, 5);
        for (int i = 0; i < ticketsToCreate; i++) {

            TicketCreateRequest createRequest = new TicketCreateRequest(faker.lorem()
                .sentence(10), faker.lorem()
                .sentence(30), faker.options()
                .option(TicketPriority.values())
                .name(), faker.options()
                .option(TicketDepartment.values())
                .name(), faker.bool()
                .bool());

            webTestClient.post()
                .uri(TICKET_PATH + "/organization/{organizationId}", organizationId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createRequest), TicketCreateRequest.class)
                .headers(header -> header.setBearerAuth(jwtToken))
                .exchange()
                .expectStatus()
                .isOk();
        }

        // when
        EntityExchangeResult<APIResponse<List<TicketDTO>>> result = webTestClient.method(HttpMethod.GET)
            .uri(TICKET_PATH + "/organization" + "/{organizationId}", organizationId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<List<TicketDTO>>>() {
            })
            .returnResult();

        // then
        assert result.getResponseBody() != null;

        APIResponse<List<TicketDTO>> apiResponse = result.getResponseBody();
        List<TicketDTO> ticketDTOS = apiResponse.getResult();

        assertThat(apiResponse.getResponseType()).isEqualTo(APIResponseType.SUCCESS);
        assertThat(ticketDTOS.size()).isEqualTo(ticketsToCreate);
    }

    @Test
    void canCreateTicketForOrganization() {
        // given
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

        OrganizationDTO organizationDTO = response.getResult();
        Long organizationId = organizationDTO.id();

        TicketCreateRequest createRequest = new TicketCreateRequest(faker.lorem()
            .sentence(10), faker.lorem()
            .sentence(30), faker.options()
            .option(TicketPriority.values())
            .name(), faker.options()
            .option(TicketDepartment.values())
            .name(), faker.bool()
            .bool());

        // when
        EntityExchangeResult<APIResponse<TicketDTO>> result = webTestClient.post()
            .uri(TICKET_PATH + "/organization" + "/{organizationId}", organizationId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(createRequest), TicketCreateRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<TicketDTO>>() {
            })
            .returnResult();

        // then
        assert result.getResponseBody() != null;

        APIResponse<TicketDTO> apiResponse = result.getResponseBody();
        TicketDTO ticketDTO = apiResponse.getResult();

        assertThat(apiResponse.getResponseType()).isEqualTo(APIResponseType.SUCCESS);
        assertThat(ticketDTO.title()).isEqualTo(createRequest.title());
        assertThat(ticketDTO.open()).isEqualTo(createRequest.open());
        assertThat(ticketDTO.createdAt()).isNotNull();
        assertThat(ticketDTO.updatedAt()).isNotNull();
        assertThat(ticketDTO.publisher()).isNotNull();
    }

    @Test
    void canGetTicket() {
        // given
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

        OrganizationDTO organizationDTO = response.getResult();
        Long organizationId = organizationDTO.id();

        TicketCreateRequest createRequest = new TicketCreateRequest(faker.lorem()
            .sentence(10), faker.lorem()
            .sentence(30), faker.options()
            .option(TicketPriority.values())
            .name(), faker.options()
            .option(TicketDepartment.values())
            .name(), faker.bool()
            .bool());

        EntityExchangeResult<APIResponse<TicketDTO>> ticketResult = webTestClient.post()
            .uri(TICKET_PATH + "/organization" + "/{organizationId}", organizationId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(createRequest), TicketCreateRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<TicketDTO>>() {
            })
            .returnResult();

        assert ticketResult.getResponseBody() != null;

        Long ticketIdToGet = ticketResult.getResponseBody()
            .getResult()
            .id();

        // when
        EntityExchangeResult<APIResponse<TicketDTO>> result = webTestClient.get()
            .uri(TICKET_PATH + "/{ticketId}", ticketIdToGet)
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<TicketDTO>>() {
            })
            .returnResult();

        // then
        assert result.getResponseBody() != null;

        APIResponse<TicketDTO> apiResponse = result.getResponseBody();
        TicketDTO ticketDTO = apiResponse.getResult();

        assertThat(apiResponse.getResponseType()).isEqualTo(APIResponseType.SUCCESS);
        assertThat(ticketDTO.organizationId()).isEqualTo(organizationId);
        assertThat(ticketDTO.organizationName()).isEqualTo(organizationName);
        assertThat(ticketDTO.title()).isEqualTo(createRequest.title());
        assertThat(ticketDTO.open()).isEqualTo(createRequest.open());
        assertThat(ticketDTO.createdAt()).isNotNull();
        assertThat(ticketDTO.updatedAt()).isNotNull();
        assertThat(ticketDTO.publisher()).isNotNull();
    }

    @Test
    void canUpdateTicket() {
        // given
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

        OrganizationDTO organizationDTO = response.getResult();
        Long organizationId = organizationDTO.id();

        TicketCreateRequest createRequest = new TicketCreateRequest(faker.lorem()
            .sentence(10), faker.lorem()
            .sentence(30), faker.options()
            .option(TicketPriority.values())
            .name(), faker.options()
            .option(TicketDepartment.values())
            .name(), faker.bool()
            .bool());

        EntityExchangeResult<APIResponse<TicketDTO>> ticketResult = webTestClient.post()
            .uri(TICKET_PATH + "/organization/{organizationId}", organizationId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(createRequest), TicketCreateRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<TicketDTO>>() {
            })
            .returnResult();

        assert ticketResult.getResponseBody() != null;

        Long ticketIdToUpdate = ticketResult.getResponseBody()
            .getResult()
            .id();

        // Get organization user
        EntityExchangeResult<APIResponse<OrganizationUserDTO>> organizationUserResult = webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path(ORGANIZATION_USER_PATH)
                .queryParam("organizationId", organizationId)
                .queryParam("email", email)
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
        TicketUpdateRequest updateRequest = new TicketUpdateRequest(organizationUserId, faker.lorem()
            .sentence(10), faker.lorem()
            .sentence(30), faker.options()
            .option(TicketPriority.values())
            .name(), faker.options()
            .option(TicketDepartment.values())
            .name(), faker.bool()
            .bool(), null);

        EntityExchangeResult<APIResponse<TicketDTO>> result = webTestClient.put()
            .uri(TICKET_PATH + "/{ticketId}", ticketIdToUpdate)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(updateRequest), TicketUpdateRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<TicketDTO>>() {
            })
            .returnResult();

        // then
        assert result.getResponseBody() != null;

        APIResponse<TicketDTO> apiResponse = result.getResponseBody();
        TicketDTO ticketDTO = apiResponse.getResult();

        assertThat(apiResponse.getResponseType()).isEqualTo(APIResponseType.SUCCESS);
        assertThat(ticketDTO.title()).isEqualTo(updateRequest.title());
        assertThat(ticketDTO.open()).isEqualTo(updateRequest.open());
        assertThat(ticketDTO.createdAt()).isNotNull();
        assertThat(ticketDTO.updatedAt()).isNotNull();
        assertThat(ticketDTO.publisher()).isNotNull();
    }

    @Test
    void canDeleteTicket() {
        // given
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

        OrganizationDTO organizationDTO = response.getResult();
        Long organizationId = organizationDTO.id();

        TicketCreateRequest createRequest = new TicketCreateRequest(faker.lorem()
            .sentence(10), faker.lorem()
            .sentence(30), faker.options()
            .option(TicketPriority.values())
            .name(), faker.options()
            .option(TicketDepartment.values())
            .name(), faker.bool()
            .bool());

        EntityExchangeResult<APIResponse<TicketDTO>> ticketResult = webTestClient.post()
            .uri(TICKET_PATH + "/organization/{organizationId}", organizationId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(createRequest), TicketCreateRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<TicketDTO>>() {
            })
            .returnResult();

        assert ticketResult.getResponseBody() != null;

        Long ticketIdToDelete = ticketResult.getResponseBody()
            .getResult()
            .id();

        // Get organization user
        EntityExchangeResult<APIResponse<OrganizationUserDTO>> organizationUserResult = webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path(ORGANIZATION_USER_PATH)
                .queryParam("organizationId", organizationId)
                .queryParam("email", email)
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
        EntityExchangeResult<APIResponse<Long>> result = webTestClient.delete()
            .uri(uriBuilder -> uriBuilder.path(TICKET_PATH + "/" + ticketIdToDelete)
                .queryParam("organizationUserId", organizationUserId)
                .build())
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<Long>>() {
            })
            .returnResult();

        // then
        assert result.getResponseBody() != null;

        APIResponse<Long> apiResponse = result.getResponseBody();
        Long deletedTicketId = apiResponse.getResult();

        assertThat(apiResponse.getResponseType()).isEqualTo(APIResponseType.SUCCESS);

        assertThat(ticketRepository.findById(deletedTicketId)).isNotPresent();
    }

}