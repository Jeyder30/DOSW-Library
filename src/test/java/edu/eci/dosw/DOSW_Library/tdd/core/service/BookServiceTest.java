package edu.eci.dosw.DOSW_Library.tdd.core.service;

import edu.eci.dosw.DOSW_Library.persistence.entity.BookEntity;
import edu.eci.dosw.DOSW_Library.persistence.mapper.BookEntityMapperImpl;
import edu.eci.dosw.DOSW_Library.persistence.repository.BookRepository;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository, new BookEntityMapperImpl());
    }

    @Test
    void getAllBooksShouldMapRepositoryEntities() {
        when(bookRepository.findAll()).thenReturn(List.of(
                entity(UUID.randomUUID(), "Clean Code", 2, 2),
                entity(UUID.randomUUID(), "Refactoring", 1, 0)
        ));

        List<Book> books = bookService.getAllBooks();

        assertEquals(2, books.size());
        assertEquals("Clean Code", books.get(0).getTitle());
        assertEquals("Refactoring", books.get(1).getTitle());
    }

    @Test
    void getAvailableBooksShouldFilterUnavailableBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(
                entity(UUID.randomUUID(), "Clean Code", 2, 2),
                entity(UUID.randomUUID(), "Refactoring", 1, 0)
        ));

        List<Book> books = bookService.getAvailableBooks();

        assertEquals(1, books.size());
        assertEquals("Clean Code", books.get(0).getTitle());
    }

    @Test
    void getBookByIdShouldReturnMappedBook() {
        UUID bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(entity(bookId, "Clean Code", 3, 2)));

        Book book = bookService.getBookById(bookId.toString());

        assertEquals(bookId.toString(), book.getId());
        assertEquals("Clean Code", book.getTitle());
        assertEquals(2, book.getAvailableCopies());
    }

    @Test
    void addBookShouldAssignAnIdAndPersistTheEntity() {
        when(bookRepository.save(any(BookEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book savedBook = bookService.addBook(book("Clean Code", "Robert C. Martin", "9780132350884", 4, 4));

        ArgumentCaptor<BookEntity> captor = ArgumentCaptor.forClass(BookEntity.class);
        verify(bookRepository).save(captor.capture());

        assertNotNull(captor.getValue().getId());
        assertEquals("Clean Code", savedBook.getTitle());
        assertEquals(4, savedBook.getAvailableCopies());
    }

    @Test
    void updateBookShouldOverwriteTheExistingEntity() {
        UUID bookId = UUID.randomUUID();
        BookEntity existingEntity = entity(bookId, "Old Title", 2, 1);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingEntity));
        when(bookRepository.save(existingEntity)).thenReturn(existingEntity);

        Book updatedBook = bookService.updateBook(
                bookId.toString(),
                book("New Title", "New Author", "9780201485677", 6, 3)
        );

        assertEquals("New Title", existingEntity.getTitle());
        assertEquals("New Author", existingEntity.getAuthor());
        assertEquals("9780201485677", existingEntity.getIsbn());
        assertEquals(6, existingEntity.getTotalCopies());
        assertEquals(3, existingEntity.getAvailableCopies());
        assertEquals("New Title", updatedBook.getTitle());
    }

    @Test
    void getBookEntityByIdShouldThrowWhenBookDoesNotExist() {
        UUID bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookEntityById(bookId.toString()));
    }

    private Book book(String title, String author, String isbn, int totalCopies, int availableCopies) {
        return Book.builder()
                .title(title)
                .author(author)
                .isbn(isbn)
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .build();
    }

    private BookEntity entity(UUID id, String title, int totalCopies, int availableCopies) {
        return BookEntity.builder()
                .id(id)
                .title(title)
                .author("Author")
                .isbn("isbn-" + id)
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .build();
    }
}
