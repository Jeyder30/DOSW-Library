package edu.eci.dosw.DOSW_Library.tdd.persistence.nonrelational.adapter;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.persistence.document.LoanDocument;
import edu.eci.dosw.DOSW_Library.tdd.persistence.nonrelational.mapper.LoanDocumentMapper;
import edu.eci.dosw.DOSW_Library.tdd.persistence.port.LibraryBookRepository;
import edu.eci.dosw.DOSW_Library.tdd.persistence.port.LibraryLoanRepository;
import edu.eci.dosw.DOSW_Library.tdd.persistence.port.LibraryUserRepository;
import edu.eci.dosw.DOSW_Library.tdd.persistence.repository.mongo.LoanMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
@Profile("mongo")
@RequiredArgsConstructor
public class LibraryLoanRepositoryMongo implements LibraryLoanRepository {

    private final LoanMongoRepository loanMongoRepository;
    private final LibraryUserRepository libraryUserRepository;
    private final LibraryBookRepository libraryBookRepository;

    @Override
    public Loan save(Loan loan) {
        LoanDocument doc = LoanDocumentMapper.toDocument(loan);
        loanMongoRepository.findById(loan.getId()).ifPresent(existing -> doc.setHistory(existing.getHistory()));
        LoanDocument saved = loanMongoRepository.save(doc);
        return hydrate(saved);
    }

    @Override
    public Optional<Loan> findById(String id) {
        return loanMongoRepository.findById(id).map(this::hydrate);
    }

    @Override
    public List<Loan> findAll() {
        return StreamSupport.stream(loanMongoRepository.findAll().spliterator(), false)
                .map(this::hydrate)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        loanMongoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return loanMongoRepository.existsById(id);
    }

    @Override
    public List<Loan> findAllByUserId(String userId) {
        return loanMongoRepository.findAllByUserId(userId).stream()
                .map(this::hydrate)
                .toList();
    }

    @Override
    public long countByUserIdAndStatus(String userId, Status status) {
        return loanMongoRepository.countByUserIdAndStatus(userId, status);
    }

    @Override
    public boolean existsByBookIdAndStatus(String bookId, Status status) {
        return loanMongoRepository.existsByBookIdAndStatus(bookId, status);
    }

    private Loan hydrate(LoanDocument d) {
        User user = libraryUserRepository.findById(d.getUserId()).orElse(null);
        Book book = libraryBookRepository.findById(d.getBookId()).orElse(null);
        return LoanDocumentMapper.toLoan(d, user, book);
    }
}
