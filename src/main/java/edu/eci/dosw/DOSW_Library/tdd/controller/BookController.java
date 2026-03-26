package edu.eci.dosw.DOSW_Library.tdd.controller;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.BookRequestDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.dto.BookResponseDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.mapper.BookApiMapper;
import edu.eci.dosw.DOSW_Library.tdd.core.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Operaciones sobre libros")
public class BookController {
    private final BookService bookService;
    private final BookApiMapper bookApiMapper;

    @Operation(summary = "Crear un libro")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('LIBRARIAN')")
    public BookResponseDTO createBook(@Valid @RequestBody BookRequestDTO dto) {
        return bookApiMapper.toResponse(bookService.addBook(bookApiMapper.toDomain(dto)));
    }

    @Operation(summary = "Actualizar un libro")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public BookResponseDTO updateBook(@PathVariable String id, @Valid @RequestBody BookRequestDTO dto) {
        return bookApiMapper.toResponse(bookService.updateBook(id, bookApiMapper.toDomain(dto)));
    }

    @Operation(summary = "Listar libros")
    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'USER')")
    public List<BookResponseDTO> getBooks() {
        return bookService.getAllBooks().stream()
                .map(bookApiMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Listar libros disponibles")
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'USER')")
    public List<BookResponseDTO> getAvailableBooks() {
        return bookService.getAvailableBooks().stream()
                .map(bookApiMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Consultar un libro por id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'USER')")
    public BookResponseDTO getBook(@PathVariable String id) {
        return bookApiMapper.toResponse(bookService.getBookById(id));
    }
}
