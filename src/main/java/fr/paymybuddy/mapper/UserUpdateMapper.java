package fr.paymybuddy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.paymybuddy.dto.UserUpdateDTO;
import fr.paymybuddy.model.User;

@Mapper(componentModel = "spring")
public interface UserUpdateMapper {

    public UserUpdateDTO toUserUpdateDTO(User user);

    @Mapping(target = "id", ignore = true)
    public User toUser(UserUpdateDTO userUpdateDTO);

}