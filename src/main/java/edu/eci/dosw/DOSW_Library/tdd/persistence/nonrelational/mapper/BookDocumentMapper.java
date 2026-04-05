package edu.eci.dosw.DOSW_Library.tdd.persistence.nonrelational.mapper;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.persistence.document.BookAvailability;
import edu.eci.dosw.DOSW_Library.tdd.persistence.document.BookDocument;
import edu.eci.dosw.DOSW_Library.tdd.persistence.document.BookInventoryStatus;

public final class BookDocumentMapper {

    private BookDocumentMapper() {
    }

    public static Book toBook(BookDocument d) {
        if (d == null) {
            return null;
        }
        BookAvailability a = d.getAvailability();
        int total = a != null ? a.getTotalCopies() : 0;
        int available = a != null ? a.getAvailableCopies() : 0;
        return Book.builder()
                .id(d.getId())
                .title(d.getTitle())
                .author(d.getAuthor())
                .isbn(d.getIsbn())
                .totalCopies(total)
                .availableCopies(available)
                .build();
    }

    public static BookDocument toDocument(Book book) {
        if (book == null) {
            return null;
        }
        int loaned = Math.max(0, book.getTotalCopies() - book.getAvailableCopies());
        BookAvailability availability = BookAvailability.builder()
                .status(BookInventoryStatus.ACTIVE)
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .loanedCopies(loaned)
                .build();
        return BookDocument.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .availability(availability)
                .build();
    }
}
