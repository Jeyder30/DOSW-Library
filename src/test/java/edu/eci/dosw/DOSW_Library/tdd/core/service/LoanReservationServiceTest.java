package edu.eci.dosw.DOSW_Library.tdd.core.service;

import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanNotFoundException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Role;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.repository.LoanRepository;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * En este dominio de biblioteca, una "reserva" se modela como un préstamo ({@code Loan}).
 */
@SpringBootTest
@ActiveProfiles("test")
class LoanReservationServiceTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanAndSeedAdmin() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Default Librarian")
                .username("admin")
                .password(passwordEncoder.encode("Admin123*"))
                .role(Role.LIBRARIAN)
                .build());
    }

    @Test
    void dadoUnaReservaRegistrada_cuandoConsultoPorId_entoncesEsExitosoYValidoElId() {
        String bookId = seedBook();
        User borrower = seedBorrower("ana", "Ana123*");

        var created = loanService.createLoan(borrower.getUsername(), Role.USER, null, bookId);
        assertNotNull(created.getId());

        var found = loanService.getLoanById(created.getId());
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void dadoNoHayReservas_cuandoConsultoListado_entoncesNoHayResultados() {
        assertTrue(loanService.getAllLoans().isEmpty());
    }

    @Test
    void dadoNoHayReservas_cuandoCreoUna_entoncesLaCreacionEsExitosa() {
        String bookId = seedBook();
        User borrower = seedBorrower("luis", "Luis123*");

        var created = loanService.createLoan(borrower.getUsername(), Role.USER, null, bookId);

        assertNotNull(created.getId());
        assertFalse(created.getId().isBlank());
        assertEquals(1, loanService.getAllLoans().size());
    }

    @Test
    void dadoUnaReserva_cuandoElimino_entoncesLaEliminacionEsExitosa() {
        String bookId = seedBook();
        User borrower = seedBorrower("maria", "Maria123*");
        var created = loanService.createLoan(borrower.getUsername(), Role.USER, null, bookId);

        loanService.deleteLoanById(created.getId());

        assertThrows(LoanNotFoundException.class, () -> loanService.getLoanById(created.getId()));
    }

    @Test
    void dadoUnaReserva_cuandoEliminoYConsultoListado_entoncesNoHayResultados() {
        String bookId = seedBook();
        User borrower = seedBorrower("pedro", "Pedro123*");
        var created = loanService.createLoan(borrower.getUsername(), Role.USER, null, bookId);

        loanService.deleteLoanById(created.getId());

        assertTrue(loanService.getAllLoans().isEmpty());
    }

    private String seedBook() {
        Book book = bookService.addBook(Book.builder()
                .title("Libro test")
                .author("Autor")
                .isbn("9990001112223")
                .totalCopies(3)
                .availableCopies(3)
                .build());
        return book.getId();
    }

    private User seedBorrower(String username, String rawPassword) {
        return userService.addUser(User.builder()
                .name("Usuario " + username)
                .username(username)
                .password(rawPassword)
                .role(Role.USER)
                .build());
    }
}
