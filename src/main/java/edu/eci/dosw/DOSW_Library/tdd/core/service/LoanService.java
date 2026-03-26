package edu.eci.dosw.DOSW_Library.tdd.core.service;

import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanAlreadyReturnedException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanNotFoundException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.DOSW_Library.tdd.core.validator.LoanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final UserService userService;
    private final BookService bookService;
    private final List<Loan> loans = new ArrayList<>();

    public Loan createLoan(String userId, String bookId) {

        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);

        LoanValidator.validate(user, book, loans);

        Loan loan = new Loan(IdGeneratorUtil.generateId(), book, user);
        loans.add(loan);

        return loan;
    }

    public Loan getLoanById(String loanId) {
        return loans.stream()
                .filter(l -> l.getId().equals(loanId))
                .findFirst()
                .orElseThrow(() -> new LoanNotFoundException("Loan with id " + loanId + " was not found"));
    }

    public Loan returnBook(String loanId) {
        Loan loan = getLoanById(loanId);
        if (loan.getStatus() == Status.RETURNED) {
            throw new LoanAlreadyReturnedException("Loan with id " + loanId + " was already returned");
        }

        loan.returnBook();
        return loan;
    }

    public List<Loan> getAllLoans() {
        return Collections.unmodifiableList(loans);
    }
}
