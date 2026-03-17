package edu.eci.dosw.DOSW_Library.service;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.service.BookService;
import edu.eci.dosw.DOSW_Library.tdd.core.service.LoanService;
import edu.eci.dosw.DOSW_Library.tdd.core.service.UserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {

    @Test
    void shouldFailWhenReturningNonExistingLoan() {
        LoanService loanService = new LoanService(new UserService(), new BookService());

        assertThrows(RuntimeException.class, () -> {
            loanService.returnBook("999");
        });
    }

    @Test
    void shouldReturnAllUsers() {
        UserService service = new UserService();
        service.addUser(new User("1", "Juan"));

        assertFalse(service.getAllUsers().isEmpty());
    }
}
