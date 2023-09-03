package com.jonathand.issuecrush.organization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrganizationAction {
    ADD_USER(OrganizationRole.ADMIN),
    REMOVE_USER(OrganizationRole.ADMIN),
    DELETE_TICKET(OrganizationRole.ADMIN),
    UPDATE_TICKET(OrganizationRole.MODERATOR),
    DELETE_TICKET_COMMENT(OrganizationRole.ADMIN),
    UPDATE_TICKET_COMMENT(OrganizationRole.ADMIN),
    ;

    private final OrganizationRole roleRequired;
}
