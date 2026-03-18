package edu.eci.dosw.DOSW_Library.mapper;

import edu.eci.dosw.DOSW_Library.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanMapperTest {
    @Test
    void shouldMapLoanToDTO() {
        User user = new User("1", "Juan");
        Book book = new Book("1", "Clean Code", "Martin");
        Loan loan = new Loan("1", book, user);

        var dto = LoanMapper.toDTO(loan);

        assertEquals("1", dto.getUserId());
        assertEquals("ACTIVE", dto.getStatus());
    }
}
