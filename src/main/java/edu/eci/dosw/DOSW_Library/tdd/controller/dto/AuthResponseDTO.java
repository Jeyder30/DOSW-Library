package edu.eci.dosw.DOSW_Library.tdd.controller.dto;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String tokenType;
    private long expiresIn;
    private String userId;
    private String username;
    private Role role;
}
