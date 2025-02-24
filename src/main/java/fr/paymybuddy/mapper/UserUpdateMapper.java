package fr.paymybuddy.mapper;

import org.mapstruct.Mapping;

import fr.paymybuddy.dto.UserFromDTO;
import fr.paymybuddy.model.User;

public interface UserUpdateMapper {

    public UserFromDTO toUserUpdateDTO(User user);

    @Mapping(target = "id", ignore = true)
    public User toUser(UserFromDTO userUpdateDTO);
}