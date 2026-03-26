package edu.eci.dosw.DOSW_Library.tdd.controller;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.mapper.UserMapper;
import edu.eci.dosw.DOSW_Library.tdd.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operaciones sobre usuarios")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Crear un usuario")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody UserDTO dto) {
        return UserMapper.toDTO(userService.addUser(UserMapper.toModel(dto)));
    }

    @Operation(summary = "Listar usuarios")
    @GetMapping
    public List<UserDTO> getUsers() {
        return userService.getAllUsers().stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    @Operation(summary = "Consultar un usuario por id")
    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable String id) {
        return UserMapper.toDTO(userService.getUserById(id));
    }
}
