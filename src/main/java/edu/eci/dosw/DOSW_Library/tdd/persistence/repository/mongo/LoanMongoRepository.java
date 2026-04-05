package edu.eci.dosw.DOSW_Library.tdd.persistence.repository.mongo;

import edu.eci.dosw.DOSW_Library.tdd.persistence.document.LoanDocument;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LoanMongoRepository extends MongoRepository<LoanDocument, String> {
    List<LoanDocument> findAllByUserId(String userId);

    long countByUserIdAndStatus(String userId, Status status);

    boolean existsByBookIdAndStatus(String bookId, Status status);
}
