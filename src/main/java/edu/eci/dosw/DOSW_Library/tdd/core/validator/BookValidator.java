package edu.eci.dosw.DOSW_Library.tdd.core.validator;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.util.ValidationUtil;

public class BookValidator {
    public static void validate(Book book) {
        ValidationUtil.requireNonNull(book, "Book cannot be null");
        ValidationUtil.requireNonEmpty(book.getId(), "Book id is required");
        ValidationUtil.requireNonEmpty(book.getTitle(), "Title is required");
        ValidationUtil.requireNonEmpty(book.getAuthor(), "Author is required");
    }
}
