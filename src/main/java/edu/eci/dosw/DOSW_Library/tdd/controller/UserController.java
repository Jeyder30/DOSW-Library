package edu.eci.dosw.DOSW_Library.tdd.controller;

import edu.eci.dosw.DOSW_Library.security.AppUserPrincipal;
import edu.eci.dosw.DOSW_Library.tdd.controller.dto.UserRequestDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.dto.UserResponseDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.mapper.UserApiMapper;
import edu.eci.dosw.DOSW_Library.tdd.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operaciones sobre usuarios")
public class UserController {
    private final UserService userService;
    private final UserApiMapper userApiMapper;

    @Operation(summary = "Crear un usuario")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('LIBRARIAN')")
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO dto) {
        return userApiMapper.toResponse(userService.addUser(userApiMapper.toDomain(dto)));
    }

    @Operation(summary = "Listar usuarios")
    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<UserResponseDTO> getUsers() {
        return userService.getAllUsers().stream()
                .map(userApiMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Consultar un usuario por id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public UserResponseDTO getUser(@PathVariable String id) {
        return userApiMapper.toResponse(userService.getUserById(id));
    }

    @Operation(summary = "Consultar el usuario autenticado")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'USER')")
    public UserResponseDTO getCurrentUser(@AuthenticationPrincipal AppUserPrincipal principal) {
        return userApiMapper.toResponse(userService.getUserById(principal.getId()));
    }
}
