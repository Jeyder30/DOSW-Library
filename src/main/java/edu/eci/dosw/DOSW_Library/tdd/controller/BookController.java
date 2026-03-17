package edu.eci.dosw.DOSW_Library.tdd.controller;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService = new BookService();

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        bookService.addBook(book);
        return book;
    }

    @GetMapping
    public List<Book> getBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Book getBook(@PathVariable String id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    public BookDTO createBook(@RequestBody BookDTO dto) {
        Book book = BookMapper.toModel(dto);
        bookService.addBook(book);
        return BookMapper.toDTO(book);
    }
}
