package edu.eci.dosw.DOSW_Library.security;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.AuthRequestDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.dto.AuthResponseDTO;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthResponseDTO login(AuthRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        AppUserPrincipal principal = (AppUserPrincipal) userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(principal);

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMs())
                .userId(principal.getId())
                .username(principal.getUsername())
                .role(principal.getRole())
                .build();
    }
}
