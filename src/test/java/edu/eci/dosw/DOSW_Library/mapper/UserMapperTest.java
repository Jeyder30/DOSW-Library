package edu.eci.dosw.DOSW_Library.mapper;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.mapper.UserMapper;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    @Test
    void shouldMapToModel() {
        UserDTO dto = new UserDTO("1", "Juan");

        User user = UserMapper.toModel(dto);

        assertEquals("Juan", user.getName());
    }

    @Test
    void shouldMapToDTO() {
        User user = new User("1", "Juan");

        UserDTO dto = UserMapper.toDTO(user);

        assertEquals("1", dto.getId());
    }
}
