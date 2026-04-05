package edu.eci.dosw.DOSW_Library.tdd.persistence.relational.adapter;

import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.persistence.port.LibraryUserRepository;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.mapper.UserEntityMapper;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Repository
@Profile("jpa")
@RequiredArgsConstructor
public class LibraryUserRepositoryJpa implements LibraryUserRepository {

    private final UserRepository jpaUserRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public User save(User user) {
        UserEntity entity = userEntityMapper.toEntity(user);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        return userEntityMapper.toDomain(jpaUserRepository.save(entity));
    }

    @Override
    public Optional<User> findById(String id) {
        return jpaUserRepository.findById(UUID.fromString(id)).map(userEntityMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return StreamSupport.stream(jpaUserRepository.findAll().spliterator(), false)
                .map(userEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        jpaUserRepository.deleteById(UUID.fromString(id));
    }

    @Override
    public boolean existsById(String id) {
        return jpaUserRepository.existsById(UUID.fromString(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username).map(userEntityMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }
}
