package edu.eci.dosw.DOSW_Library.persistence.mapper;

import edu.eci.dosw.DOSW_Library.persistence.entity.BookEntity;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface BookEntityMapper {

    @Mapping(target = "id", expression = "java(mapUuidToString(entity.getId()))")
    Book toDomain(BookEntity entity);

    @Mapping(target = "id", expression = "java(mapStringToUuid(domain.getId()))")
    BookEntity toEntity(Book domain);

    default String mapUuidToString(UUID value) {
        return value != null ? value.toString() : null;
    }

    default UUID mapStringToUuid(String value) {
        return value != null ? UUID.fromString(value) : null;
    }
}
