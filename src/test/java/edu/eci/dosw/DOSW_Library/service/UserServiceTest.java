package edu.eci.dosw.DOSW_Library.service;

import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.service.UserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTest {
    @Test
    void shouldAddAndGetUser() {
        UserService service = new UserService();
        User user = new User("1", "Juan");

        service.addUser(user);

        User result = service.getUserById("1");

        assertEquals("Juan", result.getName());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        UserService service = new UserService();

        assertThrows(RuntimeException.class, () -> {
            service.getUserById("99");
        });
    }
}
