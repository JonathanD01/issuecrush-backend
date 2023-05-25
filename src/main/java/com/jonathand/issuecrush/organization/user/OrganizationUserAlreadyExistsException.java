package com.jonathand.issuecrush.organization.user;

public class OrganizationUserAlreadyExistsException extends RuntimeException {

    public OrganizationUserAlreadyExistsException() {
        super("OrganizationUser already exists");
    }

}
