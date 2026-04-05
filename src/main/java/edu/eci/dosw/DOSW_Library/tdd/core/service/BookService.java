package edu.eci.dosw.DOSW_Library.tdd.core.service;

import edu.eci.dosw.DOSW_Library.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.validator.BookValidator;
import edu.eci.dosw.DOSW_Library.tdd.persistence.port.LibraryBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final LibraryBookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Book> getAvailableBooks() {
        return bookRepository.findAll().stream()
                .filter(book -> book.getAvailableCopies() > 0)
                .toList();
    }

    @Transactional(readOnly = true)
    public Book getBookById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " was not found"));
    }

    @Transactional
    public Book addBook(Book book) {
        BookValidator.validate(book);
        book.setId(UUID.randomUUID().toString());
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(String id, Book book) {
        BookValidator.validate(book);
        Book existing = getBookById(id);
        existing.setTitle(book.getTitle());
        existing.setAuthor(book.getAuthor());
        existing.setIsbn(book.getIsbn());
        existing.setTotalCopies(book.getTotalCopies());
        existing.setAvailableCopies(book.getAvailableCopies());
        return bookRepository.save(existing);
    }

    @Transactional
    public Book adjustAvailableCopies(String id, int delta) {
        Book book = getBookById(id);
        book.setAvailableCopies(book.getAvailableCopies() + delta);
        return bookRepository.save(book);
    }
}
