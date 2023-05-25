package com.jonathand.issuecrush.ticket.property;

import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.ticket.TicketDepartment;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket_properties")
public class TicketProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    @Column(name = "department")
    @Enumerated(EnumType.STRING)
    private TicketDepartment department;

    @ManyToOne
    @JoinColumn(name = "assigned_agent_id")
    @Nullable
    private OrganizationUser assigned_agent;

}
