package com.jonathand.issuecrush.organization.user;

import java.util.function.Function;

public class OrganizationUserDTOMapper implements Function<OrganizationUser, OrganizationUserDTO> {

    @Override
    public OrganizationUserDTO apply(OrganizationUser organizationUser) {
        if (organizationUser == null) {
            return null;
        }

        return new OrganizationUserDTO(
            organizationUser.getId(),
            organizationUser.getUser()
                .getEmail(),
            organizationUser.getUser()
                .getFirstName(),
            organizationUser.getUser()
                .getLastName(),
            organizationUser.getRole()
                .name()
                .toUpperCase());
    }

}
