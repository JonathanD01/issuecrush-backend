package com.jonathand.issuecrush.ticket.comment;

import java.util.Date;

import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.ticket.Ticket;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket_comments")
public class TicketComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "Ticket cannot be null")
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne
    @NotNull(message = "OrganizationUser cannot be null")
    @JoinColumn(name = "user_id")
    private OrganizationUser publisher;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @NotNull(message = "Content cannot be null")
    @Column(columnDefinition = "TEXT")
    private String content;

}
