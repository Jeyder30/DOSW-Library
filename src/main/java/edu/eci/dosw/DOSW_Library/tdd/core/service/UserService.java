package edu.eci.dosw.DOSW_Library.tdd.core.service;

import edu.eci.dosw.DOSW_Library.persistence.entity.UserEntity;
import edu.eci.dosw.DOSW_Library.persistence.mapper.UserEntityMapper;
import edu.eci.dosw.DOSW_Library.persistence.repository.UserRepository;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.DuplicateUsernameException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userEntityMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public User getUserById(String id) {
        return userEntityMapper.toDomain(getUserEntityById(id));
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userEntityMapper::toDomain)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " was not found"));
    }

    @Transactional
    public User addUser(User user) {
        UserValidator.validate(user);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateUsernameException("Username " + user.getUsername() + " is already in use");
        }
        UserEntity entity = userEntityMapper.toEntity(user);
        entity.setId(UUID.randomUUID());
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        return userEntityMapper.toDomain(userRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public UserEntity getUserEntityById(String id) {
        return userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " was not found"));
    }

    @Transactional(readOnly = true)
    public UserEntity getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " was not found"));
    }

    @Transactional
    public void ensureBootstrapLibrarian(User user) {
        if (!userRepository.existsByUsername(user.getUsername())) {
            addUser(user);
        }
    }
}
