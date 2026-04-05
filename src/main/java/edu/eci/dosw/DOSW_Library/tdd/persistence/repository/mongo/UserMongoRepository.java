package edu.eci.dosw.DOSW_Library.tdd.persistence.repository.mongo;

import edu.eci.dosw.DOSW_Library.tdd.persistence.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserMongoRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByUsername(String username);

    boolean existsByUsername(String username);
}
