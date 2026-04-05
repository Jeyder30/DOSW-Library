package edu.eci.dosw.DOSW_Library.security;

import edu.eci.dosw.DOSW_Library.tdd.persistence.port.LibraryUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final LibraryUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> AppUserPrincipal.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .role(user.getRole())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " was not found"));
    }
}
