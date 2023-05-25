package com.jonathand.issuecrush.ticket;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.jonathand.issuecrush.organization.Organization;
import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.ticket.body.TicketBody;
import com.jonathand.issuecrush.ticket.comment.TicketComment;
import com.jonathand.issuecrush.ticket.property.TicketProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "tickets")
public class Ticket {

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private final Set<TicketComment> comments = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "Organization cannot be null")
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne
    @Nullable
    @JoinColumn(name = "publisher_id")
    private OrganizationUser publisher;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @NotNull
    private boolean open;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @NotNull(message = "Ticket body cannot be null")
    @JoinColumn(name = "ticket_body_id")
    private TicketBody ticketBody;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @NotNull(message = "Ticket property cannot be null")
    @JoinColumn(name = "ticket_property_id")
    private TicketProperty ticketProperty;

}
