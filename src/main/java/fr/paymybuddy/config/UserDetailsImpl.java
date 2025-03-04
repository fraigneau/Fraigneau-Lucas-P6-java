package fr.paymybuddy.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import fr.paymybuddy.model.User;
import lombok.Data;

/**
 * Implementation of Spring Security's {@link UserDetails} interface.
 * <p>
 * This class serves as an adapter between the application's {@link User} model
 * and Spring Security's authentication system. It provides the necessary
 * user information to Spring Security for authentication and authorization.
 * <p>
 * The class uses Lombok's {@code @Data} annotation to automatically generate
 * common boilerplate code.
 *
 * @author PayMyBuddy
 * @version 1.0
 * @see UserDetails
 * @see User
 */
@Data
public class UserDetailsImpl implements UserDetails {

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The encoded password of the user.
     */
    private String password;

    /**
     * Constructs a new UserDetailsImpl instance from a User entity.
     * <p>
     * Maps the relevant fields from the User entity to this UserDetails
     * implementation.
     *
     * @param user the User entity to create the UserDetails from
     */
    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return the username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Returns the email address of the user.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the unique identifier of the user.
     *
     * @return the user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the authorities granted to the user.
     * <p>
     * In this implementation, no specific authorities are granted.
     *
     * @return an empty list as no authorities are assigned
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * Indicates whether the user's account has expired.
     * <p>
     * In this implementation, accounts never expire.
     *
     * @return {@code true} indicating the account is always valid
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * <p>
     * In this implementation, accounts are never locked.
     *
     * @return {@code true} indicating the account is never locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     * <p>
     * In this implementation, credentials never expire.
     *
     * @return {@code true} indicating credentials are always valid
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     * <p>
     * In this implementation, users are always enabled.
     *
     * @return {@code true} indicating the user is always enabled
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}