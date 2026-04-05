package edu.eci.dosw.DOSW_Library.tdd.persistence.port;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;

import java.util.List;

public interface LibraryLoanRepository extends GenericRepository<Loan, String> {

    List<Loan> findAllByUserId(String userId);

    long countByUserIdAndStatus(String userId, Status status);

    boolean existsByBookIdAndStatus(String bookId, Status status);
}
