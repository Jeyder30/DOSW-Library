package edu.eci.dosw.DOSW_Library.tdd.core.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoanTest {

    @Test
    void constructorShouldCreateAnActiveLoanForToday() {
        Book book = Book.builder().id("book-id").title("Clean Code").build();
        User user = User.builder().id("user-id").username("juan").build();

        Loan loan = new Loan("loan-id", book, user);

        assertEquals("loan-id", loan.getId());
        assertEquals(book, loan.getBook());
        assertEquals(user, loan.getUser());
        assertEquals(Status.ACTIVE, loan.getStatus());
        assertEquals(LocalDate.now(), loan.getLoanDate());
        assertNull(loan.getReturnDate());
    }

    @Test
    void returnBookShouldMarkTheLoanAsReturned() {
        Loan loan = Loan.builder()
                .id("loan-id")
                .status(Status.ACTIVE)
                .loanDate(LocalDate.now().minusDays(2))
                .build();

        loan.returnBook();

        assertEquals(Status.RETURNED, loan.getStatus());
        assertEquals(LocalDate.now(), loan.getReturnDate());
    }
}
