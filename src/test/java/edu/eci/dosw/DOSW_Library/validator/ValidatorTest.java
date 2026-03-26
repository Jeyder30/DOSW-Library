package edu.eci.dosw.DOSW_Library.validator;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.validator.BookValidator;
import edu.eci.dosw.DOSW_Library.tdd.core.validator.LoanValidator;
import edu.eci.dosw.DOSW_Library.tdd.core.validator.UserValidator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidatorTest {
    @Test
    void shouldValidateUser() {
        User user = new User("1", "Juan");
        assertDoesNotThrow(() -> UserValidator.validate(user));
    }

    @Test
    void shouldFailUserValidation() {
        User user = new User(null, "Juan");
        assertThrows(IllegalArgumentException.class, () -> UserValidator.validate(user));
    }

    @Test
    void shouldValidateBook() {
        Book book = new Book("1", "Clean Code", "Martin");
        assertDoesNotThrow(() -> BookValidator.validate(book));
    }

    @Test
    void shouldValidateLoan() {
        User user = new User("1", "Juan");
        Book book = new Book("1", "Clean Code", "Martin");

        assertDoesNotThrow(() -> LoanValidator.validate(user, book, List.of()));
    }

    @Test
    void shouldFailLoanValidationWhenBookAlreadyActive() {
        User user = new User("1", "Juan");
        Book book = new Book("1", "Clean Code", "Martin");
        Loan activeLoan = new Loan("loan-1", book, user);

        assertThrows(RuntimeException.class, () -> LoanValidator.validate(user, book, List.of(activeLoan)));
    }
}
