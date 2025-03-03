package fr.paymybuddy.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Integer id;
    private String username;
    private String email;
    private List<UserFriendResponseDTO> friends;

}
