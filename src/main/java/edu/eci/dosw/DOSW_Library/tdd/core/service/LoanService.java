package edu.eci.dosw.DOSW_Library.tdd.core.service;

import edu.eci.dosw.DOSW_Library.tdd.core.exception.InventoryOperationException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanAlreadyReturnedException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanNotFoundException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Role;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.util.DateUtil;
import edu.eci.dosw.DOSW_Library.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.DOSW_Library.tdd.core.validator.LoanValidator;
import edu.eci.dosw.DOSW_Library.tdd.persistence.port.LibraryLoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final UserService userService;
    private final BookService bookService;
    private final LibraryLoanRepository loanRepository;

    @Transactional
    public Loan createLoan(String authenticatedUsername, Role role, String requestedUserId, String bookId) {
        User borrower = resolveBorrower(authenticatedUsername, role, requestedUserId);
        Book bookDomain = bookService.getBookById(bookId);

        List<Loan> existingLoans = loanRepository.findAllByUserId(borrower.getId());
        LoanValidator.validate(borrower, bookDomain, existingLoans);

        bookService.adjustAvailableCopies(bookId, -1);
        Book updatedBook = bookService.getBookById(bookId);

        Loan loan = Loan.builder()
                .id(IdGeneratorUtil.generateId())
                .user(borrower)
                .book(updatedBook)
                .status(Status.ACTIVE)
                .loanDate(DateUtil.today())
                .build();

        return loanRepository.save(loan);
    }

    @Transactional(readOnly = true)
    public Loan getLoanById(String loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan with id " + loanId + " was not found"));
    }

    @Transactional
    public Loan returnBook(String loanId, String authenticatedUsername, Role role) {
        Loan loan = getLoanById(loanId);
        validateLoanAccess(loan, authenticatedUsername, role);
        if (loan.getStatus() == Status.RETURNED) {
            throw new LoanAlreadyReturnedException("Loan with id " + loanId + " was already returned");
        }

        Book book = loan.getBook();
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new InventoryOperationException("Cannot return a book beyond its total stock");
        }

        loan.setStatus(Status.RETURNED);
        loan.setReturnDate(DateUtil.today());
        bookService.adjustAvailableCopies(book.getId(), 1);
        loan.setBook(bookService.getBookById(book.getId()));
        return loanRepository.save(loan);
    }

    @Transactional(readOnly = true)
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Loan> getLoansForUser(String authenticatedUsername) {
        User user = userService.getUserByUsername(authenticatedUsername);
        return loanRepository.findAllByUserId(user.getId());
    }

    @Transactional
    public void deleteLoanById(String loanId) {
        if (!loanRepository.existsById(loanId)) {
            throw new LoanNotFoundException("Loan with id " + loanId + " was not found");
        }
        loanRepository.deleteById(loanId);
    }

    private void validateLoanAccess(Loan loan, String authenticatedUsername, Role role) {
        if (role == Role.LIBRARIAN) {
            return;
        }
        if (!loan.getUser().getUsername().equals(authenticatedUsername)) {
            throw new AccessDeniedException("You are not allowed to access this loan");
        }
    }

    private User resolveBorrower(String authenticatedUsername, Role role, String requestedUserId) {
        if (role == Role.LIBRARIAN && requestedUserId != null && !requestedUserId.isBlank()) {
            return userService.getUserById(requestedUserId);
        }
        return userService.getUserByUsername(authenticatedUsername);
    }
}
