package edu.eci.dosw.DOSW_Library.tdd.controller;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.DOSW_Library.tdd.core.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Operaciones sobre libros")
public class BookController {
    private final BookService bookService;

    @Operation(summary = "Crear un libro")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(@Valid @RequestBody BookDTO dto) {
        return BookMapper.toDTO(bookService.addBook(BookMapper.toModel(dto)));
    }

    @Operation(summary = "Listar libros")
    @GetMapping
    public List<BookDTO> getBooks() {
        return bookService.getAllBooks().stream()
                .map(BookMapper::toDTO)
                .toList();
    }

    @Operation(summary = "Consultar un libro por id")
    @GetMapping("/{id}")
    public BookDTO getBook(@PathVariable String id) {
        return BookMapper.toDTO(bookService.getBookById(id));
    }
}
