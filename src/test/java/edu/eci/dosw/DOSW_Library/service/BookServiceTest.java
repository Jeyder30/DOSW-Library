package edu.eci.dosw.DOSW_Library.service;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.service.BookService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookServiceTest {
    @Test
    void shouldAddAndGetBook() {
        BookService service = new BookService();
        Book book = new Book("1", "Clean Code", "Robert Martin");

        service.addBook(book);

        Book result = service.getBookById("1");

        assertEquals("1", result.getId());
    }

    @Test
    void shouldThrowWhenBookNotFound() {
        BookService service = new BookService();

        assertThrows(RuntimeException.class, () -> {
            service.getBookById("99");
        });
    }
}
