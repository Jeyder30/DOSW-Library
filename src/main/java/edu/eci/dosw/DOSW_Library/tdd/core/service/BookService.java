package edu.eci.dosw.DOSW_Library.tdd.core.service;

import edu.eci.dosw.DOSW_Library.persistence.entity.BookEntity;
import edu.eci.dosw.DOSW_Library.persistence.mapper.BookEntityMapper;
import edu.eci.dosw.DOSW_Library.persistence.repository.BookRepository;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.validator.BookValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookEntityMapper bookEntityMapper;

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookEntityMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Book> getAvailableBooks() {
        return bookRepository.findAll().stream()
                .map(bookEntityMapper::toDomain)
                .filter(book -> book.getAvailableCopies() > 0)
                .toList();
    }

    @Transactional(readOnly = true)
    public Book getBookById(String id) {
        return bookEntityMapper.toDomain(getBookEntityById(id));
    }

    @Transactional
    public Book addBook(Book book) {
        BookValidator.validate(book);
        BookEntity entity = bookEntityMapper.toEntity(book);
        entity.setId(UUID.randomUUID());
        return bookEntityMapper.toDomain(bookRepository.save(entity));
    }

    @Transactional
    public Book updateBook(String id, Book book) {
        BookValidator.validate(book);
        BookEntity entity = getBookEntityById(id);
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setIsbn(book.getIsbn());
        entity.setTotalCopies(book.getTotalCopies());
        entity.setAvailableCopies(book.getAvailableCopies());
        return bookEntityMapper.toDomain(bookRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public BookEntity getBookEntityById(String id) {
        return bookRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " was not found"));
    }
}
