package fr.paymybuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for Spring Security settings.
 * <p>
 * This class configures security aspects of the application including:
 * authentication, authorization, form login configuration, and password
 * encoding.
 * It defines which endpoints are publicly accessible and which require
 * authentication.
 * 
 * @author PayMyBuddy
 * @version 1.0
 * @see SecurityFilterChain
 * @see PasswordEncoder
 */
@Configuration
public class SecurityConfig {

        /**
         * Default constructor for SecurityConfig.
         */
        public SecurityConfig() {
        }

        /**
         * Configures the security filter chain for HTTP requests.
         * <p>
         * This method defines the security rules for the application:
         * - Public access to login, signup, and CSS resources
         * - Authentication requirement for all other endpoints
         * - Custom login page configuration
         * - Logout behavior configuration
         *
         * @param http the HttpSecurity object to configure
         * @return the built SecurityFilterChain
         * @throws Exception if an error occurs during configuration
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/login", "/signup", "/signup-processing",
                                                                "/style.css")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(login -> login
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/profil", true)
                                                .failureUrl("/login?error")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                return http.build();
        }

        /**
         * Creates a password encoder bean for secure password handling.
         * <p>
         * This method configures BCrypt as the password encoding algorithm,
         * which is used for securely storing and verifying user passwords.
         *
         * @return a BCryptPasswordEncoder instance
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}