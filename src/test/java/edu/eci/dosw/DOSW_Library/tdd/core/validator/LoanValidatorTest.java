package edu.eci.dosw.DOSW_Library.tdd.core.validator;

import edu.eci.dosw.DOSW_Library.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Role;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoanValidatorTest {

    @Test
    void validateShouldRejectMissingUsers() {
        assertThrows(IllegalArgumentException.class, () -> LoanValidator.validate(null, book(2), List.of()));
    }

    @Test
    void validateShouldRejectMissingBooks() {
        assertThrows(IllegalArgumentException.class, () -> LoanValidator.validate(user("user-1"), null, List.of()));
    }

    @Test
    void validateShouldRejectUnavailableBooks() {
        assertThrows(
                BookNotAvailableException.class,
                () -> LoanValidator.validate(user("user-1"), book(0), List.of())
        );
    }

    @Test
    void validateShouldRejectUsersWithThreeActiveLoans() {
        User user = user("user-1");
        List<Loan> loans = List.of(
                loan("loan-1", user, Status.ACTIVE),
                loan("loan-2", user, Status.ACTIVE),
                loan("loan-3", user, Status.ACTIVE)
        );

        assertThrows(LoanLimitExceededException.class, () -> LoanValidator.validate(user, book(1), loans));
    }

    @Test
    void validateShouldAllowUsersWithReturnedLoansOnly() {
        User user = user("user-1");
        List<Loan> loans = List.of(loan("loan-1", user, Status.RETURNED));

        assertDoesNotThrow(() -> LoanValidator.validate(user, book(1), loans));
    }

    private User user(String id) {
        return User.builder()
                .id(id)
                .name("User " + id)
                .username("user" + id)
                .password("encoded")
                .role(Role.USER)
                .build();
    }

    private Book book(int availableCopies) {
        return Book.builder()
                .id("book-1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .totalCopies(3)
                .availableCopies(availableCopies)
                .build();
    }

    private Loan loan(String id, User user, Status status) {
        return Loan.builder()
                .id(id)
                .user(user)
                .book(book(1))
                .status(status)
                .build();
    }
}
