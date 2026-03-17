package edu.eci.dosw.DOSW_Library.tdd.core.validator;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.util.ValidationUtil;

public class LoanValidator {
    public static void validate(User user, Book book) {
        ValidationUtil.requireNonNull(user, "User not found");
        ValidationUtil.requireNonNull(book, "Book not found");
    }
}
