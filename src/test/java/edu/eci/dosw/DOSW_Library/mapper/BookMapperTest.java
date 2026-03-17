package edu.eci.dosw.DOSW_Library.mapper;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookMapperTest {

    @Test
    void shouldMapToModel() {
        BookDTO dto = new BookDTO("1", "Clean Code", "Martin");

        Book book = BookMapper.toModel(dto);

        assertEquals("1", book.getId());
    }

    @Test
    void shouldMapToDTO() {
        Book book = new Book("1", "Clean Code", "Martin");

        BookDTO dto = BookMapper.toDTO(book);

        assertEquals("Clean Code", dto.getTitle());
    }
}
