package edu.eci.dosw.DOSW_Library.service;

import edu.eci.dosw.DOSW_Library.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanAlreadyReturnedException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanNotFoundException;
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

        LoanService loanService = new LoanService(userService, bookService);

        Loan loan = loanService.createLoan("1", "1");

        assertNotNull(loan);
        assertEquals(Status.ACTIVE, loan.getStatus());
        assertNotNull(loan.getLoanDate());
    }

    @Test
    void shouldReturnBookSuccessfully() {
        UserService userService = new UserService();
        BookService bookService = new BookService();

        User user = new User("1", "Juan");
        Book book = new Book("1", "Clean Code", "Martin");

        userService.addUser(user);
        bookService.addBook(book);

        LoanService loanService = new LoanService(userService, bookService);

        Loan loan = loanService.createLoan("1", "1");

        loanService.returnBook(loan.getId());

        assertEquals(Status.RETURNED, loan.getStatus());
        assertNotNull(loan.getReturnDate());
    }

    @Test
    void shouldFailWhenLoanNotFound() {
        LoanService loanService = new LoanService(new UserService(), new BookService());

        assertThrows(LoanNotFoundException.class, () -> {
            loanService.returnBook("999");
        });
    }

    @Test
    void shouldFailWhenBookIsAlreadyLoaned() {
        UserService userService = new UserService();
        BookService bookService = new BookService();

        userService.addUser(new User("1", "Juan"));
        userService.addUser(new User("2", "Maria"));
        bookService.addBook(new Book("1", "Clean Code", "Martin"));

        LoanService loanService = new LoanService(userService, bookService);
        loanService.createLoan("1", "1");

        assertThrows(BookNotAvailableException.class, () -> loanService.createLoan("2", "1"));
    }

    @Test
    void shouldFailWhenUserReachesLoanLimit() {
        UserService userService = new UserService();
        BookService bookService = new BookService();
        userService.addUser(new User("1", "Juan"));
        bookService.addBook(new Book("1", "Clean Code", "Martin"));
        bookService.addBook(new Book("2", "Refactoring", "Fowler"));
        bookService.addBook(new Book("3", "DDD", "Evans"));
        bookService.addBook(new Book("4", "TDD", "Beck"));

        LoanService loanService = new LoanService(userService, bookService);
        loanService.createLoan("1", "1");
        loanService.createLoan("1", "2");
        loanService.createLoan("1", "3");

        assertThrows(LoanLimitExceededException.class, () -> loanService.createLoan("1", "4"));
    }

    @Test
    void shouldFailWhenReturningLoanTwice() {
        UserService userService = new UserService();
        BookService bookService = new BookService();
        userService.addUser(new User("1", "Juan"));
        bookService.addBook(new Book("1", "Clean Code", "Martin"));

        LoanService loanService = new LoanService(userService, bookService);
        Loan loan = loanService.createLoan("1", "1");
        loanService.returnBook(loan.getId());

        assertThrows(LoanAlreadyReturnedException.class, () -> loanService.returnBook(loan.getId()));
    }
}
