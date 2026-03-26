package edu.eci.dosw.DOSW_Library.security;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Role;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootstrapDataConfig {

    @Bean
    CommandLineRunner bootstrapLibrarian(
            UserService userService,
            @Value("${app.bootstrap.librarian.username}") String username,
            @Value("${app.bootstrap.librarian.password}") String password,
            @Value("${app.bootstrap.librarian.name}") String name
    ) {
        return args -> userService.ensureBootstrapLibrarian(User.builder()
                .name(name)
                .username(username)
                .password(password)
                .role(Role.LIBRARIAN)
                .build());
    }
}
