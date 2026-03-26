package edu.eci.dosw.DOSW_Library.tdd.controller.mapper;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.BookRequestDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.dto.BookResponseDTO;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookApiMapper {
    @Mapping(target = "id", ignore = true)
    Book toDomain(BookRequestDTO dto);
    BookResponseDTO toResponse(Book book);
}
