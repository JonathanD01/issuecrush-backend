package com.jonathand.issuecrush.user;

import com.jonathand.issuecrush.auth.EmailAlreadyTakenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserUtil {

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves the current user details from the security context.
     *
     * @return the UserDetails object representing the current authenticated user
     */
    public UserDetails getCurrentUserDetails() {
        return (UserDetails) SecurityContextHolder.getContext()
                                                  .getAuthentication()
                                                  .getPrincipal();
    }

    /**
     * Retrieves a user by their email.
     *
     * @param userEmail The email of the user.
     * @return The user associated with the email.
     * @throws UsernameNotFoundException If the user is not found.
     */
    public User getUserByEmail(String userEmail) {
        return userRepository
            .findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Validates if a user with the specified email exists in the system.
     *
     * @param userEmail The email of the user to validate.
     * @throws UsernameNotFoundException If the user with the specified email is not found.
     */
    public void validateUserExistsByEmail(String userEmail) {
        if (!userRepository.existsByEmail(userEmail)) {
            throw new UsernameNotFoundException("User not found");
        }
    }

    /**
     * Validates that the specified email address does not exist in the user repository.
     * If the email already exists, an EmailAlreadyTakenException is thrown.
     *
     * @param email the email address to validate
     * @throws EmailAlreadyTakenException if the email already exists in the user repository
     */
    public void validateEmailDoesNotExist(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyTakenException(email);
        }
    }

}
