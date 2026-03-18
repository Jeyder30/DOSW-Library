package edu.eci.dosw.DOSW_Library.tdd.controller.mapper;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;

public class BookMapper {

    public static Book toModel(BookDTO dto) {
        return new Book(dto.getId(), dto.getTitle(), dto.getAuthor());
    }

    public static BookDTO toDTO(Book book) {
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor());
    }
}
