package edu.eci.dosw.DOSW_Library.tdd.persistence.port;

import edu.eci.dosw.DOSW_Library.tdd.core.model.User;

import java.util.Optional;

public interface LibraryUserRepository extends GenericRepository<User, String> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
