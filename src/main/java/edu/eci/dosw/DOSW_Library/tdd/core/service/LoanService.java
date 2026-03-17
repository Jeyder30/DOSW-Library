package edu.eci.dosw.DOSW_Library.tdd.core.service;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.validator.LoanValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LoanService {

    @Autowired
    public UserService userService;

    @Autowired
    public BookService bookService;

    private List<Loan> loans = new ArrayList<>();

    public Loan createLoan(String userId, String bookId) {

        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);

        LoanValidator.validate(user, book);

        Loan loan = new Loan(UUID.randomUUID().toString(), book, user);
        loans.add(loan);

        return loan;
    }

    public void returnBook(String loanId) {
        Loan loan = loans.stream()
                .filter(l -> l.getId().equals(loanId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.returnBook();
    }

    public List<Loan> getAllLoans() {
        return loans;
    }
}
