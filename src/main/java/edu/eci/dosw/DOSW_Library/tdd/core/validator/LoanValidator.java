package edu.eci.dosw.DOSW_Library.tdd.core.validator;

import edu.eci.dosw.DOSW_Library.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.util.ValidationUtil;

import java.util.List;

public class LoanValidator {
    private static final int MAX_ACTIVE_LOANS = 3;

    private LoanValidator() {
    }

    public static void validate(User user, Book book, List<Loan> loans) {
        ValidationUtil.requireNonNull(user, "User not found");
        ValidationUtil.requireNonNull(book, "Book not found");
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book is not available for loan");
        }

        long activeLoansByUser = loans.stream()
                .filter(loan -> loan.getUser().getId().equals(user.getId()))
                .filter(loan -> loan.getStatus() == Status.ACTIVE)
                .count();

        if (activeLoansByUser >= MAX_ACTIVE_LOANS) {
            throw new LoanLimitExceededException("User has reached the maximum number of active loans");
        }
    }
}
