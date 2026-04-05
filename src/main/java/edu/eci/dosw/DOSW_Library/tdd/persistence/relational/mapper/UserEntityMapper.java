package edu.eci.dosw.DOSW_Library.tdd.persistence.relational.mapper;

import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    @Mapping(target = "id", expression = "java(mapUuidToString(entity.getId()))")
    User toDomain(UserEntity entity);

    @Mapping(target = "id", expression = "java(mapStringToUuid(domain.getId()))")
    UserEntity toEntity(User domain);

    default String mapUuidToString(UUID value) {
        return value != null ? value.toString() : null;
    }

    default UUID mapStringToUuid(String value) {
        return value != null ? UUID.fromString(value) : null;
    }
}
