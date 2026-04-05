package edu.eci.dosw.DOSW_Library.security;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.AuthRequestDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.dto.AuthResponseDTO;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.InvalidCredentialsException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(authenticationManager, userDetailsService, jwtService);
    }

    @Test
    void loginShouldAuthenticateAndBuildTheJwtResponse() {
        AuthRequestDTO request = AuthRequestDTO.builder()
                .username("juan")
                .password("User123*")
                .build();
        AppUserPrincipal principal = AppUserPrincipal.builder()
                .id("user-id")
                .username("juan")
                .password("encoded")
                .role(Role.USER)
                .build();

        when(userDetailsService.loadUserByUsername("juan")).thenReturn(principal);
        when(jwtService.generateToken(principal)).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3_600_000L);

        AuthResponseDTO response = authService.login(request);

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("juan", "User123*"));
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3_600_000L, response.getExpiresIn());
        assertEquals("user-id", response.getUserId());
        assertEquals("juan", response.getUsername());
        assertEquals(Role.USER, response.getRole());
    }

    @Test
    void loginShouldTranslateBadCredentialsToADomainException() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad credentials"));

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(AuthRequestDTO.builder().username("juan").password("wrong").build())
        );

        verifyNoInteractions(userDetailsService, jwtService);
    }
}
