package edu.eci.dosw.DOSW_Library.tdd.persistence.nonrelational.adapter;

import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.persistence.nonrelational.mapper.UserDocumentMapper;
import edu.eci.dosw.DOSW_Library.tdd.persistence.port.LibraryUserRepository;
import edu.eci.dosw.DOSW_Library.tdd.persistence.repository.mongo.UserMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
@Profile("mongo")
@RequiredArgsConstructor
public class LibraryUserRepositoryMongo implements LibraryUserRepository {

    private final UserMongoRepository userMongoRepository;

    @Override
    public User save(User user) {
        return UserDocumentMapper.toUser(userMongoRepository.save(UserDocumentMapper.toDocument(user)));
    }

    @Override
    public Optional<User> findById(String id) {
        return userMongoRepository.findById(id).map(UserDocumentMapper::toUser);
    }

    @Override
    public List<User> findAll() {
        return StreamSupport.stream(userMongoRepository.findAll().spliterator(), false)
                .map(UserDocumentMapper::toUser)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        userMongoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return userMongoRepository.existsById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userMongoRepository.findByUsername(username).map(UserDocumentMapper::toUser);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMongoRepository.existsByUsername(username);
    }
}
