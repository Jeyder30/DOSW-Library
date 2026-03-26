package edu.eci.dosw.DOSW_Library.tdd.controller.mapper;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.UserRequestDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.dto.UserResponseDTO;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserApiMapper {
    @Mapping(target = "id", ignore = true)
    User toDomain(UserRequestDTO dto);
    UserResponseDTO toResponse(User user);
}
