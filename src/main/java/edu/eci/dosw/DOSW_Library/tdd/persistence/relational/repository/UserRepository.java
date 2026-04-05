package edu.eci.dosw.DOSW_Library.tdd.persistence.relational.repository;

import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
