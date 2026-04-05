package edu.eci.dosw.DOSW_Library.security;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Role;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void generateTokenShouldExposeTheExpectedClaims() {
        JwtService jwtService = new JwtService("01234567890123456789012345678901", 60_000L);
        AppUserPrincipal principal = AppUserPrincipal.builder()
                .id("user-id")
                .username("juan")
                .password("encoded")
                .role(Role.USER)
                .build();

        String token = jwtService.generateToken(principal);

        assertEquals("juan", jwtService.extractUsername(token));
        assertEquals(Role.USER, jwtService.extractRole(token));
        assertTrue(jwtService.isTokenValid(token, principal));
        assertEquals(60_000L, jwtService.getExpirationMs());
    }

    @Test
    void constructorShouldDecodeShortSecretsBeforeRejectingWeakKeys() {
        assertThrows(WeakKeyException.class, () -> new JwtService("YWJjZA==", 1_000L));
    }
}
