package edu.eci.dosw.DOSW_Library.security;

import edu.eci.dosw.DOSW_Library.persistence.entity.UserEntity;
import edu.eci.dosw.DOSW_Library.persistence.repository.UserRepository;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsernameShouldReturnAnApplicationPrincipal() {
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Juan Perez")
                .username("juan")
                .password("encoded")
                .role(Role.LIBRARIAN)
                .build();
        when(userRepository.findByUsername("juan")).thenReturn(Optional.of(user));

        AppUserPrincipal principal = (AppUserPrincipal) customUserDetailsService.loadUserByUsername("juan");

        assertEquals(user.getId().toString(), principal.getId());
        assertEquals("juan", principal.getUsername());
        assertEquals("encoded", principal.getPassword());
        assertEquals(Role.LIBRARIAN, principal.getRole());
    }

    @Test
    void loadUserByUsernameShouldThrowWhenTheUserDoesNotExist() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("missing"));
    }
}
