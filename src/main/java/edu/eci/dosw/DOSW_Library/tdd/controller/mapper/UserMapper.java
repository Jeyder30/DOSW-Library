package edu.eci.dosw.DOSW_Library.tdd.controller.mapper;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;

public class UserMapper {
    public static User toModel(UserDTO dto) {
        return new User(dto.getId(), dto.getName());
    }

    public static UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName());
    }
}
