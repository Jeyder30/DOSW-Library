package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.tdd.controller.GlobalExceptionHandler;
import edu.eci.dosw.DOSW_Library.tdd.controller.LoanController;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanAlreadyReturnedException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
@Import(GlobalExceptionHandler.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Test
    void shouldCreateLoan() throws Exception {
        Loan loan = Loan.builder()
                .id("loan-1")
                .user(new User("user-1", "Juan"))
                .book(new Book("book-1", "Clean Code", "Martin"))
                .status(Status.ACTIVE)
                .loanDate(LocalDate.of(2026, 3, 26))
                .build();

        when(loanService.createLoan("user-1", "book-1")).thenReturn(loan);

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "user-1",
                                  "bookId": "book-1"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("loan-1"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturnBadRequestWhenLoanCannotBeCreated() throws Exception {
        when(loanService.createLoan("user-1", "book-1")).thenThrow(new BookNotAvailableException("Book is not available for loan"));

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "user-1",
                                  "bookId": "book-1"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Book is not available for loan"));
    }

    @Test
    void shouldListLoans() throws Exception {
        Loan loan = Loan.builder()
                .id("loan-1")
                .user(new User("user-1", "Juan"))
                .book(new Book("book-1", "Clean Code", "Martin"))
                .status(Status.ACTIVE)
                .loanDate(LocalDate.of(2026, 3, 26))
                .build();

        when(loanService.getAllLoans()).thenReturn(List.of(loan));

        mockMvc.perform(get("/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("loan-1"));
    }

    @Test
    void shouldReturnLoan() throws Exception {
        Loan loan = Loan.builder()
                .id("loan-1")
                .user(new User("user-1", "Juan"))
                .book(new Book("book-1", "Clean Code", "Martin"))
                .status(Status.RETURNED)
                .loanDate(LocalDate.of(2026, 3, 26))
                .returnDate(LocalDate.of(2026, 3, 27))
                .build();

        when(loanService.returnBook("loan-1")).thenReturn(loan);

        mockMvc.perform(put("/loans/loan-1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));
    }

    @Test
    void shouldFailWhenLoanAlreadyReturned() throws Exception {
        when(loanService.returnBook("loan-1")).thenThrow(new LoanAlreadyReturnedException("Loan with id loan-1 was already returned"));

        mockMvc.perform(put("/loans/loan-1/return"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Loan with id loan-1 was already returned"));
    }
}
