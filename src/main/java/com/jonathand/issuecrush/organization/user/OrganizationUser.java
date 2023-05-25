package com.jonathand.issuecrush.organization.user;

import com.jonathand.issuecrush.organization.Organization;
import com.jonathand.issuecrush.organization.OrganizationRole;
import com.jonathand.issuecrush.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "organization_users")
public class OrganizationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "User cannot be null")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @NotNull(message = "Organization cannot be null")
    @JoinColumn(name = "org_id")
    private Organization organization;

    @NotNull(message = "Role cannot be null")
    @Enumerated(EnumType.STRING)
    private OrganizationRole role;

}
