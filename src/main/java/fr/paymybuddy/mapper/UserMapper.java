package fr.paymybuddy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.paymybuddy.dto.UserFormDTO;
import fr.paymybuddy.dto.UserFriendDTO;
import fr.paymybuddy.model.User;

@Mapper
public interface UserMapper {
    // --------------- FormDTO ---------------
    public UserFormDTO toUserFormDTO(User user);

    @Mapping(target = "id", ignore = true)
    public User toUser(UserFormDTO userUpdateDTO);

    // --------------- FriendDTO ---------------
    // public UserFriendDTO toUserFriendDTO(User user);

    // public User toUser(UserFriendDTO friendDTO);
}