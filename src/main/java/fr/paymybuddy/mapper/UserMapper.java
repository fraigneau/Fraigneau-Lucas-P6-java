package fr.paymybuddy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.paymybuddy.dto.UserFormRequestDTO;
import fr.paymybuddy.model.User;

@Mapper
public interface UserMapper {

    public UserFormRequestDTO toUserFormDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "friends", ignore = true)
    @Mapping(target = "receivedTransactions", ignore = true)
    @Mapping(target = "sentTransactions", ignore = true)
    @Mapping(target = "balance", ignore = true)
    public User toUser(UserFormRequestDTO userUpdateDTO);

}