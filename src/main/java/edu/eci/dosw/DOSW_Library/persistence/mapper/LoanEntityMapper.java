package edu.eci.dosw.DOSW_Library.persistence.mapper;

import edu.eci.dosw.DOSW_Library.persistence.entity.LoanEntity;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {UserEntityMapper.class, BookEntityMapper.class})
public interface LoanEntityMapper {

    @Mapping(target = "id", expression = "java(mapUuidToString(entity.getId()))")
    Loan toDomain(LoanEntity entity);

    @Mapping(target = "id", expression = "java(mapStringToUuid(domain.getId()))")
    LoanEntity toEntity(Loan domain);

    default String mapUuidToString(UUID value) {
        return value != null ? value.toString() : null;
    }

    default UUID mapStringToUuid(String value) {
        return value != null ? UUID.fromString(value) : null;
    }
}
