package com.jonathand.issuecrush.ticket.comment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.UserRegisterExtractor;
import com.jonathand.issuecrush.auth.AuthenticationResponse;
import com.jonathand.issuecrush.organization.OrganizationDTO;
import com.jonathand.issuecrush.organization.OrganizationNewRequest;
import com.jonathand.issuecrush.organization.user.OrganizationUserDTO;
import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseType;
import com.jonathand.issuecrush.ticket.TicketCreateRequest;
import com.jonathand.issuecrush.ticket.TicketDTO;
import com.jonathand.issuecrush.ticket.TicketDepartment;
import com.jonathand.issuecrush.ticket.property.TicketPriority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TicketCommentIntegrationTest {

    private static final String AUTHENTICATION_PATH = "/api/v1/auth";

    private static final String TICKET_PATH = "/api/v1/tickets";

    private static final String ORGANIZATION_PATH = "/api/v1/organizations";

    private static final String ORGANIZATION_USER_PATH = "/api/v1/organization-users";

    private static final String TICKET_COMMENTS_PATH = "/api/v1/ticket-comments";

    private final Faker faker = new Faker();

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TicketCommentRepository ticketCommentRepository;

    @Test
    void canCreateTicketCommentForTicket() {
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

        APIResponse<OrganizationDTO> organizationResponse = organizationResult.getResponseBody();

        assert organizationResponse != null;

        OrganizationDTO organizationDTO = organizationResponse.getResult();
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

        APIResponse<TicketDTO> ticketResponse = ticketResult.getResponseBody();

        assert ticketResponse != null;
        TicketDTO ticketDTO = ticketResponse.getResult();
        Long ticketId = ticketDTO.id();

        TicketCommentCreateRequest commentCreateRequest = new TicketCommentCreateRequest(faker.lorem()
            .sentence(30));

        // when
        EntityExchangeResult<APIResponse<TicketCommentDTO>> result = webTestClient.post()
            .uri(TICKET_COMMENTS_PATH + "/{ticketId}", ticketId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(commentCreateRequest), TicketCommentCreateRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<TicketCommentDTO>>() {
            })
            .returnResult();

        // then
        assert result.getResponseBody() != null;

        APIResponse<TicketCommentDTO> apiResponse = result.getResponseBody();
        TicketCommentDTO ticketCommentDTO = apiResponse.getResult();

        assertThat(apiResponse.getResponseType()).isEqualTo(APIResponseType.SUCCESS);
        assertThat(ticketCommentDTO.content()).isEqualTo(commentCreateRequest.content());
        assertThat(ticketCommentDTO.publisher()
            .email()).isEqualTo(email);
        assertThat(ticketCommentDTO.createdAt()).isNotNull();
        assertThat(ticketCommentDTO.updatedAt()).isNotNull();
    }

    @Test
    void canGetTicketComment() {
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

        APIResponse<OrganizationDTO> organizationResponse = organizationResult.getResponseBody();

        assert organizationResponse != null;

        OrganizationDTO organizationDTO = organizationResponse.getResult();
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

        APIResponse<TicketDTO> ticketResponse = ticketResult.getResponseBody();

        assert ticketResponse != null;
        TicketDTO ticketDTO = ticketResponse.getResult();
        Long ticketId = ticketDTO.id();

        TicketCommentCreateRequest commentCreateRequest = new TicketCommentCreateRequest(faker.lorem()
            .sentence(30));

        EntityExchangeResult<APIResponse<TicketCommentDTO>> ticketCommentResultToGet = webTestClient.post()
            .uri(TICKET_COMMENTS_PATH + "/{ticketId}", ticketId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(commentCreateRequest), TicketCommentCreateRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<TicketCommentDTO>>() {
            })
            .returnResult();

        assert ticketCommentResultToGet.getResponseBody() != null;

        TicketCommentDTO ticketCommentDTO = ticketCommentResultToGet.getResponseBody()
            .getResult();

        Long ticketCommentId = ticketCommentDTO.id();

        // when
        EntityExchangeResult<APIResponse<TicketCommentDTO>> ticketCommentResult = webTestClient.get()
            .uri(TICKET_COMMENTS_PATH + "/comment/{ticketCommentId}", ticketCommentId)
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<TicketCommentDTO>>() {
            })
            .returnResult();


        APIResponse<TicketCommentDTO> apiResponse = ticketCommentResult.getResponseBody();
        assert apiResponse != null;

        TicketCommentDTO ticketCommentDTOToCompare = apiResponse.getResult();

        assertThat(apiResponse.getResponseType()).isEqualTo(APIResponseType.SUCCESS);
        assertThat(ticketCommentDTOToCompare).isEqualTo(ticketCommentDTO);
    }

    @Test
    void canUpdateTicketComment() {
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

        APIResponse<OrganizationDTO> organizationResponse = organizationResult.getResponseBody();

        assert organizationResponse != null;

        OrganizationDTO organizationDTO = organizationResponse.getResult();
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

        APIResponse<TicketDTO> ticketResponse = ticketResult.getResponseBody();

        assert ticketResponse != null;
        TicketDTO ticketDTO = ticketResponse.getResult();
        Long ticketId = ticketDTO.id();

        TicketCommentCreateRequest commentCreateRequest = new TicketCommentCreateRequest(faker.lorem()
            .sentence(30));

        EntityExchangeResult<APIResponse<TicketCommentDTO>> ticketCommentResultToGet = webTestClient.post()
            .uri(TICKET_COMMENTS_PATH + "/{ticketId}", ticketId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(commentCreateRequest), TicketCommentCreateRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<TicketCommentDTO>>() {
            })
            .returnResult();

        assert ticketCommentResultToGet.getResponseBody() != null;

        TicketCommentDTO ticketCommentDTO = ticketCommentResultToGet.getResponseBody()
            .getResult();

        Long ticketCommentId = ticketCommentDTO.id();

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
        TicketCommentUpdateRequest updateRequest = new TicketCommentUpdateRequest(organizationUserId, faker.lorem()
            .sentence(30));

        EntityExchangeResult<APIResponse<TicketCommentDTO>> ticketCommentResult = webTestClient.put()
            .uri(TICKET_COMMENTS_PATH + "/comment/{ticketCommentId}", ticketCommentId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .body(Mono.just(updateRequest), TicketCommentUpdateRequest.class)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<TicketCommentDTO>>() {
            })
            .returnResult();


        APIResponse<TicketCommentDTO> apiResponse = ticketCommentResult.getResponseBody();
        assert apiResponse != null;

        TicketCommentDTO ticketCommentDTOToCompare = apiResponse.getResult();

        assertThat(apiResponse.getResponseType()).isEqualTo(APIResponseType.SUCCESS);
        assertThat(ticketCommentDTOToCompare).isNotEqualTo(ticketCommentDTO);
        assertThat(ticketCommentDTOToCompare.content()).isEqualTo(updateRequest.content());
    }

    @Test
    void canDeleteTicketComment() {
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

        APIResponse<OrganizationDTO> organizationResponse = organizationResult.getResponseBody();

        assert organizationResponse != null;

        OrganizationDTO organizationDTO = organizationResponse.getResult();
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

        APIResponse<TicketDTO> ticketResponse = ticketResult.getResponseBody();

        assert ticketResponse != null;
        TicketDTO ticketDTO = ticketResponse.getResult();
        Long ticketId = ticketDTO.id();

        TicketCommentCreateRequest commentCreateRequest = new TicketCommentCreateRequest(faker.lorem()
            .sentence(30));

        EntityExchangeResult<APIResponse<TicketCommentDTO>> ticketCommentResultToGet = webTestClient.post()
            .uri(TICKET_COMMENTS_PATH + "/{ticketId}", ticketId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(commentCreateRequest), TicketCommentCreateRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<TicketCommentDTO>>() {
            })
            .returnResult();

        assert ticketCommentResultToGet.getResponseBody() != null;

        TicketCommentDTO ticketCommentDTO = ticketCommentResultToGet.getResponseBody()
            .getResult();

        Long ticketCommentId = ticketCommentDTO.id();

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
        EntityExchangeResult<APIResponse<Long>> deletedIdResult = webTestClient.delete()
            .uri(uriBuilder -> uriBuilder.path(TICKET_COMMENTS_PATH + "/comment/" + ticketCommentId)
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

        APIResponse<Long> apiResponse = deletedIdResult.getResponseBody();
        assert apiResponse != null;

        Long deletedTicketCommentId = apiResponse.getResult();

        assertThat(ticketCommentRepository.findById(deletedTicketCommentId)).isNotPresent();
        assertThat(apiResponse.getResponseType()).isEqualTo(APIResponseType.SUCCESS);
        assertThat(deletedTicketCommentId).isEqualTo(ticketCommentDTO.id());
    }

}