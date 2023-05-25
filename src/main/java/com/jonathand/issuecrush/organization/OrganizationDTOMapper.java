package com.jonathand.issuecrush.organization;

import java.util.function.Function;

import com.jonathand.issuecrush.user.UserDTOMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrganizationDTOMapper implements Function<Organization, OrganizationDTO> {

    private final UserDTOMapper userDTOMapper;

    @Override
    public OrganizationDTO apply(Organization organization) {
        return new OrganizationDTO(
            organization.getId(),
            userDTOMapper.apply(organization.getCreator()),
            organization.getName(),
            organization.getCreatedAt(),
            organization.getUpdatedAt());
    }

}
