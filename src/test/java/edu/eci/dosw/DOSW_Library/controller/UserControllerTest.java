package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.tdd.controller.GlobalExceptionHandler;
import edu.eci.dosw.DOSW_Library.tdd.controller.UserController;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {
        when(userService.addUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "user-1",
                                  "name": "Juan Perez"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("user-1"))
                .andExpect(jsonPath("$.name").value("Juan Perez"));
    }

    @Test
    void shouldListUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(new User("user-1", "Juan Perez")));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("user-1"));
    }

    @Test
    void shouldReturnNotFoundForMissingUser() throws Exception {
        when(userService.getUserById("missing")).thenThrow(new UserNotFoundException("User with id missing was not found"));

        mockMvc.perform(get("/users/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id missing was not found"));
    }
}
