package pl.kurs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.kurs.dto.CreateRoleDto;
import pl.kurs.dto.RoleDto;
import pl.kurs.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    Role dtoToEntity(CreateRoleDto dto);

    RoleDto entityToDto(Role role);
}
