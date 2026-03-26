package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.tdd.controller.BookController;
import edu.eci.dosw.DOSW_Library.tdd.controller.GlobalExceptionHandler;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(GlobalExceptionHandler.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    void shouldCreateBook() throws Exception {
        when(bookService.addBook(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "book-1",
                                  "title": "Clean Code",
                                  "author": "Robert C. Martin"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("book-1"))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void shouldListBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(new Book("book-1", "Clean Code", "Robert C. Martin")));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("book-1"));
    }

    @Test
    void shouldReturnNotFoundForMissingBook() throws Exception {
        when(bookService.getBookById("missing")).thenThrow(new BookNotFoundException("Book with id missing was not found"));

        mockMvc.perform(get("/books/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with id missing was not found"));
    }
}
