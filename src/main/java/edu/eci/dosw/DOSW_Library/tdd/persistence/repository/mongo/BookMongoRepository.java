package edu.eci.dosw.DOSW_Library.tdd.persistence.repository.mongo;

import edu.eci.dosw.DOSW_Library.tdd.persistence.document.BookDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BookMongoRepository extends MongoRepository<BookDocument, String> {
    Optional<BookDocument> findByIsbn(String isbn);
}
