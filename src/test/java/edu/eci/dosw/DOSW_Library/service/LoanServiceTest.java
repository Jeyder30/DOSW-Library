package edu.eci.dosw.DOSW_Library.service;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.service.BookService;
import edu.eci.dosw.DOSW_Library.tdd.core.service.LoanService;
import edu.eci.dosw.DOSW_Library.tdd.core.service.UserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoanServiceTest {

    @Test
    void shouldCreateLoanSuccessfully() {
        UserService userService = new UserService();
        BookService bookService = new BookService();

        User user = new User("1", "Juan");
        Book book = new Book("1", "Clean Code", "Martin");

        userService.addUser(user);
        bookService.addBook(book);

        LoanService loanService = new LoanService();
        loanService.userService = userService;
        loanService.bookService = bookService;

        Loan loan = loanService.createLoan("1", "1");

        assertNotNull(loan);
        assertEquals(Status.ACTIVE, loan.getStatus());
    }

    @Test
    void shouldReturnBookSuccessfully() {
        UserService userService = new UserService();
        BookService bookService = new BookService();

        User user = new User("1", "Juan");
        Book book = new Book("1", "Clean Code", "Martin");

        userService.addUser(user);
        bookService.addBook(book);

        LoanService loanService = new LoanService();
        loanService.userService = userService;
        loanService.bookService = bookService;

        Loan loan = loanService.createLoan("1", "1");

        loanService.returnBook(loan.getId());

        assertEquals(Status.RETURNED, loan.getStatus());
    }

    @Test
    void shouldFailWhenLoanNotFound() {
        LoanService loanService = new LoanService();

        assertThrows(RuntimeException.class, () -> {
            loanService.returnBook("999");
        });
    }
}
