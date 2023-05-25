package com.jonathand.issuecrush.user;

import java.util.function.Function;

public class UserDTOMapper implements Function<User, UserDTO> {

    @Override
    public UserDTO apply(User user) {
        return new UserDTO(
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getUserRole()
                .name()
                .toUpperCase(),
            user.isEnabled());
    }

}
