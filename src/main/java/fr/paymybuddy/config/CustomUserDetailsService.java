package fr.paymybuddy.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fr.paymybuddy.model.User;
import fr.paymybuddy.repository.UserRepository;

/**
 * Custom implementation of the Spring Security {@link UserDetailsService}
 * interface.
 * This service is responsible for loading user-specific data for authentication
 * purposes.
 * <p>
 * It retrieves user information from the application's database using the
 * {@link UserRepository}
 * and converts it into a format compatible with Spring Security's
 * authentication system.
 * 
 * @author PayMyBuddy
 * @version 1.0
 * @see UserDetailsService
 * @see UserDetailsImpl
 * @see UserRepository
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Repository used for user data access operations.
     */
    private final UserRepository userRepository;

    /**
     * Constructs a new CustomUserDetailsService with the required repository.
     * 
     * @param userRepository the repository used to access user data
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their email address.
     * <p>
     * This method retrieves a user from the database using their email as the
     * username
     * and converts it to a {@link UserDetails} object that Spring Security can use
     * for authentication and authorization.
     *
     * @param email the email address identifying the user whose data is required
     * @return a fully populated user record (never {@code null})
     * @throws UsernameNotFoundException if the user could not be found or the user
     *                                   has no authorities
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new UserDetailsImpl(user);
    }
}