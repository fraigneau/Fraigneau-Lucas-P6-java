package fr.paymybuddy.mapper;

import fr.paymybuddy.dto.UserUpdateDTO;
import fr.paymybuddy.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-22T01:18:09+0100",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.12.1.jar, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class UserUpdateMapperImpl implements UserUpdateMapper {

    @Override
    public UserUpdateDTO toUserUpdateDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();

        userUpdateDTO.setUsername( user.getUsername() );
        userUpdateDTO.setEmail( user.getEmail() );
        userUpdateDTO.setPassword( user.getPassword() );

        return userUpdateDTO;
    }

    @Override
    public User toUser(UserUpdateDTO userUpdateDTO) {
        if ( userUpdateDTO == null ) {
            return null;
        }

        User user = new User();

        user.setUsername( userUpdateDTO.getUsername() );
        user.setEmail( userUpdateDTO.getEmail() );
        user.setPassword( userUpdateDTO.getPassword() );

        return user;
    }
}
