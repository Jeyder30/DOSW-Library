package edu.eci.dosw.DOSW_Library.tdd.core.service;

import edu.eci.dosw.DOSW_Library.persistence.entity.BookEntity;
import edu.eci.dosw.DOSW_Library.persistence.entity.LoanEntity;
import edu.eci.dosw.DOSW_Library.persistence.entity.UserEntity;
import edu.eci.dosw.DOSW_Library.persistence.mapper.LoanEntityMapper;
import edu.eci.dosw.DOSW_Library.persistence.repository.BookRepository;
import edu.eci.dosw.DOSW_Library.persistence.repository.LoanRepository;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanAlreadyReturnedException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanNotFoundException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.InventoryOperationException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Role;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.DOSW_Library.tdd.core.validator.LoanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final UserService userService;
    private final BookService bookService;
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final LoanEntityMapper loanEntityMapper;

    @Transactional
    public Loan createLoan(String authenticatedUsername, Role role, String requestedUserId, String bookId) {
        UserEntity borrower = resolveBorrower(authenticatedUsername, role, requestedUserId);
        BookEntity bookEntity = bookService.getBookEntityById(bookId);

        User borrowerDomain = userService.getUserById(borrower.getId().toString());
        Book bookDomain = bookService.getBookById(bookId);
        List<Loan> existingLoans = getLoansForValidation(borrower.getId());
        LoanValidator.validate(borrowerDomain, bookDomain, existingLoans);

        bookEntity.setAvailableCopies(bookEntity.getAvailableCopies() - 1);
        bookRepository.save(bookEntity);

        LoanEntity loanEntity = LoanEntity.builder()
                .id(UUID.fromString(IdGeneratorUtil.generateId()))
                .user(borrower)
                .book(bookEntity)
                .status(Status.ACTIVE)
                .loanDate(edu.eci.dosw.DOSW_Library.tdd.core.util.DateUtil.today())
                .build();

        return loanEntityMapper.toDomain(loanRepository.save(loanEntity));
    }

    @Transactional(readOnly = true)
    public Loan getLoanById(String loanId) {
        return loanEntityMapper.toDomain(getLoanEntityById(loanId));
    }

    @Transactional
    public Loan returnBook(String loanId, String authenticatedUsername, Role role) {
        LoanEntity loanEntity = getLoanEntityById(loanId);
        validateLoanAccess(loanEntity, authenticatedUsername, role);
        if (loanEntity.getStatus() == Status.RETURNED) {
            throw new LoanAlreadyReturnedException("Loan with id " + loanId + " was already returned");
        }

        BookEntity bookEntity = loanEntity.getBook();
        if (bookEntity.getAvailableCopies() >= bookEntity.getTotalCopies()) {
            throw new InventoryOperationException("Cannot return a book beyond its total stock");
        }

        loanEntity.setStatus(Status.RETURNED);
        loanEntity.setReturnDate(edu.eci.dosw.DOSW_Library.tdd.core.util.DateUtil.today());
        bookEntity.setAvailableCopies(bookEntity.getAvailableCopies() + 1);

        bookRepository.save(bookEntity);
        return loanEntityMapper.toDomain(loanRepository.save(loanEntity));
    }

    @Transactional(readOnly = true)
    public List<Loan> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(loanEntityMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Loan> getLoansForUser(String authenticatedUsername) {
        UserEntity userEntity = userService.getUserEntityByUsername(authenticatedUsername);
        return loanRepository.findAllByUserId(userEntity.getId()).stream()
                .map(loanEntityMapper::toDomain)
                .toList();
    }

    private LoanEntity getLoanEntityById(String loanId) {
        return loanRepository.findById(UUID.fromString(loanId))
                .orElseThrow(() -> new LoanNotFoundException("Loan with id " + loanId + " was not found"));
    }

    private UserEntity resolveBorrower(String authenticatedUsername, Role role, String requestedUserId) {
        if (role == Role.LIBRARIAN && requestedUserId != null && !requestedUserId.isBlank()) {
            return userService.getUserEntityById(requestedUserId);
        }
        return userService.getUserEntityByUsername(authenticatedUsername);
    }

    private void validateLoanAccess(LoanEntity loanEntity, String authenticatedUsername, Role role) {
        if (role == Role.LIBRARIAN) {
            return;
        }
        if (!loanEntity.getUser().getUsername().equals(authenticatedUsername)) {
            throw new AccessDeniedException("You are not allowed to access this loan");
        }
    }

    private List<Loan> getLoansForValidation(UUID userId) {
        return loanRepository.findAllByUserId(userId).stream()
                .map(loanEntityMapper::toDomain)
                .toList();
    }
}
