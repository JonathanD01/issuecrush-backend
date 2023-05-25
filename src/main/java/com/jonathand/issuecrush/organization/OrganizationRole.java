package com.jonathand.issuecrush.organization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the roles available for an organization.
 */
@RequiredArgsConstructor
@Getter
public enum OrganizationRole {
    OWNER(4, "All permissions"),
    ADMIN(3, "Can edit, delete both tickets and members." + "Can edit ticket comments"),
    MODERATOR(2, "Can edit tickets"),
    MEMBER(1, "Can create tickets");

    private final int priority;

    private final String roleDescription;

    public static OrganizationRole fromString(String roleArgument) {
        try {
            return OrganizationRole.valueOf(roleArgument.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OrganizationRoleNotFoundException(roleArgument.toUpperCase());
        }
    }

    /**
     * Checks if this role has a greater or equal priority than the specified role.
     *
     * @param roleToCheckAgainst the role to compare against
     * @return true if this role has greater or equal priority, false otherwise
     */
    public boolean hasGreaterOrEqualPriorityThan(OrganizationRole roleToCheckAgainst) {
        return this.priority >= roleToCheckAgainst.getPriority();
    }
}
