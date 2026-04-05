package edu.eci.dosw.DOSW_Library.tdd.persistence.relational.adapter;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import edu.eci.dosw.DOSW_Library.tdd.persistence.port.LibraryLoanRepository;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.mapper.LoanEntityMapper;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.repository.LoanRepository;
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
public class LibraryLoanRepositoryJpa implements LibraryLoanRepository {

    private final LoanRepository jpaLoanRepository;
    private final UserRepository jpaUserRepository;
    private final BookRepository jpaBookRepository;
    private final LoanEntityMapper loanEntityMapper;

    @Override
    public Loan save(Loan loan) {
        UserEntity user = jpaUserRepository.findById(UUID.fromString(loan.getUser().getId())).orElseThrow();
        BookEntity book = jpaBookRepository.findById(UUID.fromString(loan.getBook().getId())).orElseThrow();

        LoanEntity entity;
        UUID loanUuid = UUID.fromString(loan.getId());
        if (jpaLoanRepository.existsById(loanUuid)) {
            entity = jpaLoanRepository.findById(loanUuid).orElseThrow();
            entity.setStatus(loan.getStatus());
            entity.setLoanDate(loan.getLoanDate());
            entity.setReturnDate(loan.getReturnDate());
            entity.setUser(user);
            entity.setBook(book);
        } else {
            entity = LoanEntity.builder()
                    .id(loanUuid)
                    .user(user)
                    .book(book)
                    .status(loan.getStatus())
                    .loanDate(loan.getLoanDate())
                    .returnDate(loan.getReturnDate())
                    .build();
        }
        return loanEntityMapper.toDomain(jpaLoanRepository.save(entity));
    }

    @Override
    public Optional<Loan> findById(String id) {
        return jpaLoanRepository.findById(UUID.fromString(id)).map(loanEntityMapper::toDomain);
    }

    @Override
    public List<Loan> findAll() {
        return StreamSupport.stream(jpaLoanRepository.findAll().spliterator(), false)
                .map(loanEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        jpaLoanRepository.deleteById(UUID.fromString(id));
    }

    @Override
    public boolean existsById(String id) {
        return jpaLoanRepository.existsById(UUID.fromString(id));
    }

    @Override
    public List<Loan> findAllByUserId(String userId) {
        return jpaLoanRepository.findAllByUserId(UUID.fromString(userId)).stream()
                .map(loanEntityMapper::toDomain)
                .toList();
    }

    @Override
    public long countByUserIdAndStatus(String userId, Status status) {
        return jpaLoanRepository.countByUserIdAndStatus(UUID.fromString(userId), status);
    }

    @Override
    public boolean existsByBookIdAndStatus(String bookId, Status status) {
        return jpaLoanRepository.existsByBookIdAndStatus(UUID.fromString(bookId), status);
    }
}
