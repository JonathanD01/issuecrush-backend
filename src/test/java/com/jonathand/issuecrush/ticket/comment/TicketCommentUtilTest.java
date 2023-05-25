package com.jonathand.issuecrush.ticket.comment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TicketCommentUtilTest {

    private final Faker faker = new Faker();

    @Mock
    private TicketCommentRepository ticketCommentRepository;

    @InjectMocks
    private TicketCommentUtil underTest;

    @Test
    void canGetTicketComment() {
        // given
        Long ticketCommentId = faker.random()
            .nextLong(100L);

        Optional<TicketComment> optionalTicketComment = Optional.of(mock(TicketComment.class));

        when(ticketCommentRepository.findById(ticketCommentId)).thenReturn(optionalTicketComment);

        // when
        // then
        assertDoesNotThrow(() -> underTest.getTicketComment(ticketCommentId));
    }

    @Test
    void canGetTicketCommentWillThrowWhenNotFound() {
        // given
        Long ticketCommentId = faker.random()
            .nextLong(100L);

        // when
        // then
        assertThatThrownBy(() -> underTest.getTicketComment(ticketCommentId)).isInstanceOf(
                TicketCommentNotFoundException.class)
            .hasMessageContaining("TicketComment with id " + ticketCommentId + " was not found");
    }

}