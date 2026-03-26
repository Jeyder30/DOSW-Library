package edu.eci.dosw.DOSW_Library.tdd.controller.mapper;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;

public class UserMapper {
    public static User toModel(UserDTO dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public static UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
