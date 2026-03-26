package edu.eci.dosw.DOSW_Library.tdd.core.validator;

import edu.eci.dosw.DOSW_Library.tdd.core.exception.InventoryOperationException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.util.ValidationUtil;

public class BookValidator {
    private BookValidator() {
    }

    public static void validate(Book book) {
        ValidationUtil.requireNonNull(book, "Book cannot be null");
        ValidationUtil.requireNonEmpty(book.getTitle(), "Title is required");
        ValidationUtil.requireNonEmpty(book.getAuthor(), "Author is required");
        ValidationUtil.requireNonEmpty(book.getIsbn(), "ISBN is required");
        if (book.getTotalCopies() <= 0) {
            throw new InventoryOperationException("Total copies must be greater than 0");
        }
        if (book.getAvailableCopies() < 0) {
            throw new InventoryOperationException("Available copies cannot be negative");
        }
        if (book.getAvailableCopies() > book.getTotalCopies()) {
            throw new InventoryOperationException("Available copies cannot exceed total copies");
        }
    }
}
