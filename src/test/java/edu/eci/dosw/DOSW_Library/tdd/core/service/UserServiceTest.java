package edu.eci.dosw.DOSW_Library.tdd.core.service;

import edu.eci.dosw.DOSW_Library.persistence.entity.UserEntity;
import edu.eci.dosw.DOSW_Library.persistence.mapper.UserEntityMapperImpl;
import edu.eci.dosw.DOSW_Library.persistence.repository.UserRepository;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.DuplicateUsernameException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Role;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, new UserEntityMapperImpl(), passwordEncoder);
    }

    @Test
    void getAllUsersShouldMapRepositoryEntities() {
        when(userRepository.findAll()).thenReturn(List.of(entity(UUID.randomUUID(), "juan"), entity(UUID.randomUUID(), "maria")));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertEquals("juan", users.get(0).getUsername());
        assertEquals("maria", users.get(1).getUsername());
    }

    @Test
    void getUserByUsernameShouldReturnMappedUser() {
        UserEntity entity = entity(UUID.randomUUID(), "juan");
        when(userRepository.findByUsername("juan")).thenReturn(Optional.of(entity));

        User user = userService.getUserByUsername("juan");

        assertEquals(entity.getId().toString(), user.getId());
        assertEquals("juan", user.getUsername());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void addUserShouldEncodePasswordAndPersistANewEntity() {
        when(userRepository.existsByUsername("juan")).thenReturn(false);
        when(passwordEncoder.encode("User123*")).thenReturn("encoded-password");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.addUser(user("Juan Perez", "juan", "User123*", Role.USER));

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());

        assertNotNull(captor.getValue().getId());
        assertEquals("encoded-password", captor.getValue().getPassword());
        assertEquals("juan", savedUser.getUsername());
    }

    @Test
    void addUserShouldRejectDuplicateUsernames() {
        when(userRepository.existsByUsername("juan")).thenReturn(true);

        assertThrows(
                DuplicateUsernameException.class,
                () -> userService.addUser(user("Juan Perez", "juan", "User123*", Role.USER))
        );
    }

    @Test
    void getUserEntityByIdShouldThrowWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserEntityById(userId.toString()));
    }

    @Test
    void getUserEntityByUsernameShouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserEntityByUsername("missing"));
    }

    @Test
    void ensureBootstrapLibrarianShouldCreateTheUserWhenMissing() {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("Admin123*")).thenReturn("encoded-admin");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.ensureBootstrapLibrarian(user("Default Librarian", "admin", "Admin123*", Role.LIBRARIAN));

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void ensureBootstrapLibrarianShouldDoNothingWhenTheUserAlreadyExists() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        userService.ensureBootstrapLibrarian(user("Default Librarian", "admin", "Admin123*", Role.LIBRARIAN));

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    private User user(String name, String username, String password, Role role) {
        return User.builder()
                .name(name)
                .username(username)
                .password(password)
                .role(role)
                .build();
    }

    private UserEntity entity(UUID id, String username) {
        return UserEntity.builder()
                .id(id)
                .name("Name for " + username)
                .username(username)
                .password("encoded")
                .role(Role.USER)
                .build();
    }
}
