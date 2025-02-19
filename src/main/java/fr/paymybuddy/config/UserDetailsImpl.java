package fr.paymybuddy.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import fr.paymybuddy.model.User;

public class UserDetailsImpl implements UserDetails {
    private String email;
    private String password;
    private String username;
    private Long id;

    public UserDetailsImpl(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.id = user.getId();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
