package edu.eci.dosw.DOSW_Library.tdd.core.service;

import edu.eci.dosw.DOSW_Library.persistence.entity.BookEntity;
import edu.eci.dosw.DOSW_Library.persistence.entity.LoanEntity;
import edu.eci.dosw.DOSW_Library.persistence.entity.UserEntity;
import edu.eci.dosw.DOSW_Library.persistence.mapper.BookEntityMapperImpl;
import edu.eci.dosw.DOSW_Library.persistence.mapper.LoanEntityMapperImpl;
import edu.eci.dosw.DOSW_Library.persistence.mapper.UserEntityMapperImpl;
import edu.eci.dosw.DOSW_Library.persistence.repository.BookRepository;
import edu.eci.dosw.DOSW_Library.persistence.repository.LoanRepository;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.InventoryOperationException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanAlreadyReturnedException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.LoanNotFoundException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Role;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
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
class LoanServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    private LoanService loanService;

    @BeforeEach
    void setUp() {
        LoanEntityMapperImpl loanEntityMapper = new LoanEntityMapperImpl();
        ReflectionTestUtils.setField(loanEntityMapper, "userEntityMapper", new UserEntityMapperImpl());
        ReflectionTestUtils.setField(loanEntityMapper, "bookEntityMapper", new BookEntityMapperImpl());

        loanService = new LoanService(userService, bookService, loanRepository, bookRepository, loanEntityMapper);
    }

    @Test
    void createLoanShouldUseTheRequestedBorrowerForLibrarians() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        UserEntity borrower = userEntity(userId, "borrower");
        BookEntity bookEntity = bookEntity(bookId, 3, 3);

        when(userService.getUserEntityById(userId.toString())).thenReturn(borrower);
        when(bookService.getBookEntityById(bookId.toString())).thenReturn(bookEntity);
        when(userService.getUserById(userId.toString())).thenReturn(user(userId, "borrower"));
        when(bookService.getBookById(bookId.toString())).thenReturn(book(bookId, 3, 3));
        when(loanRepository.findAllByUserId(userId)).thenReturn(List.of());
        when(bookRepository.save(any(BookEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanRepository.save(any(LoanEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan loan = loanService.createLoan("admin", Role.LIBRARIAN, userId.toString(), bookId.toString());

        assertNotNull(loan.getId());
        assertEquals(userId.toString(), loan.getUser().getId());
        assertEquals(bookId.toString(), loan.getBook().getId());
        assertEquals(Status.ACTIVE, loan.getStatus());
        assertEquals(2, bookEntity.getAvailableCopies());
        verify(userService).getUserEntityById(userId.toString());
    }

    @Test
    void getLoanByIdShouldReturnTheMappedLoan() {
        UUID loanId = UUID.randomUUID();
        LoanEntity entity = loanEntity(loanId, userEntity(UUID.randomUUID(), "juan"), bookEntity(UUID.randomUUID(), 2, 1), Status.ACTIVE);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(entity));

        Loan loan = loanService.getLoanById(loanId.toString());

        assertEquals(loanId.toString(), loan.getId());
        assertEquals(Status.ACTIVE, loan.getStatus());
    }

    @Test
    void getLoanByIdShouldThrowWhenTheLoanDoesNotExist() {
        UUID loanId = UUID.randomUUID();
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> loanService.getLoanById(loanId.toString()));
    }

    @Test
    void returnBookShouldRejectUsersTryingToReturnSomeoneElsesLoan() {
        UUID loanId = UUID.randomUUID();
        LoanEntity entity = loanEntity(loanId, userEntity(UUID.randomUUID(), "owner"), bookEntity(UUID.randomUUID(), 2, 1), Status.ACTIVE);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(entity));

        assertThrows(AccessDeniedException.class, () -> loanService.returnBook(loanId.toString(), "other-user", Role.USER));
    }

    @Test
    void returnBookShouldRejectLoansThatWereAlreadyReturned() {
        UUID loanId = UUID.randomUUID();
        LoanEntity entity = loanEntity(loanId, userEntity(UUID.randomUUID(), "owner"), bookEntity(UUID.randomUUID(), 2, 1), Status.RETURNED);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(entity));

        assertThrows(LoanAlreadyReturnedException.class, () -> loanService.returnBook(loanId.toString(), "owner", Role.USER));
    }

    @Test
    void returnBookShouldRejectInventoryOverflow() {
        UUID loanId = UUID.randomUUID();
        LoanEntity entity = loanEntity(loanId, userEntity(UUID.randomUUID(), "owner"), bookEntity(UUID.randomUUID(), 2, 2), Status.ACTIVE);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(entity));

        assertThrows(InventoryOperationException.class, () -> loanService.returnBook(loanId.toString(), "admin", Role.LIBRARIAN));
    }

    @Test
    void returnBookShouldMarkTheLoanAsReturnedAndRestoreStock() {
        UUID loanId = UUID.randomUUID();
        BookEntity bookEntity = bookEntity(UUID.randomUUID(), 2, 1);
        LoanEntity entity = loanEntity(loanId, userEntity(UUID.randomUUID(), "owner"), bookEntity, Status.ACTIVE);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(entity));
        when(bookRepository.save(bookEntity)).thenReturn(bookEntity);
        when(loanRepository.save(entity)).thenReturn(entity);

        Loan returnedLoan = loanService.returnBook(loanId.toString(), "admin", Role.LIBRARIAN);

        assertEquals(Status.RETURNED, returnedLoan.getStatus());
        assertEquals(2, bookEntity.getAvailableCopies());
        assertEquals(LocalDate.now(), entity.getReturnDate());
    }

    @Test
    void getAllLoansShouldMapAllRepositoryResults() {
        LoanEntity entity = loanEntity(UUID.randomUUID(), userEntity(UUID.randomUUID(), "owner"), bookEntity(UUID.randomUUID(), 2, 1), Status.ACTIVE);
        when(loanRepository.findAll()).thenReturn(List.of(entity));

        List<Loan> loans = loanService.getAllLoans();

        assertEquals(1, loans.size());
        assertEquals(entity.getId().toString(), loans.get(0).getId());
    }

    @Test
    void getLoansForUserShouldLoadLoansByAuthenticatedUsername() {
        UserEntity userEntity = userEntity(UUID.randomUUID(), "owner");
        LoanEntity entity = loanEntity(UUID.randomUUID(), userEntity, bookEntity(UUID.randomUUID(), 2, 1), Status.ACTIVE);
        when(userService.getUserEntityByUsername("owner")).thenReturn(userEntity);
        when(loanRepository.findAllByUserId(userEntity.getId())).thenReturn(List.of(entity));

        List<Loan> loans = loanService.getLoansForUser("owner");

        assertEquals(1, loans.size());
        assertEquals(userEntity.getId().toString(), loans.get(0).getUser().getId());
    }

    private User user(UUID id, String username) {
        return User.builder()
                .id(id.toString())
                .name("Name for " + username)
                .username(username)
                .password("encoded")
                .role(Role.USER)
                .build();
    }

    private Book book(UUID id, int totalCopies, int availableCopies) {
        return Book.builder()
                .id(id.toString())
                .title("Book " + id)
                .author("Author")
                .isbn("isbn-" + id)
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .build();
    }

    private UserEntity userEntity(UUID id, String username) {
        return UserEntity.builder()
                .id(id)
                .name("Name for " + username)
                .username(username)
                .password("encoded")
                .role(Role.USER)
                .build();
    }

    private BookEntity bookEntity(UUID id, int totalCopies, int availableCopies) {
        return BookEntity.builder()
                .id(id)
                .title("Book " + id)
                .author("Author")
                .isbn("isbn-" + id)
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .build();
    }

    private LoanEntity loanEntity(UUID id, UserEntity user, BookEntity book, Status status) {
        return LoanEntity.builder()
                .id(id)
                .user(user)
                .book(book)
                .status(status)
                .loanDate(LocalDate.now().minusDays(1))
                .build();
    }
}
