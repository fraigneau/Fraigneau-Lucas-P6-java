package fr.paymybuddy.dto;

import fr.paymybuddy.config.UserDetailsImpl;
import fr.paymybuddy.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private User user;
    private Long id;
    private String username;
    private String email;

    public LoginResponseDTO(UserDetailsImpl user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
