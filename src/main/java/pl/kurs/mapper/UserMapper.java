package pl.kurs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.kurs.dto.CreateUserDto;
import pl.kurs.dto.UserDto;
import pl.kurs.entity.Role;
import pl.kurs.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User dtoToEntity(CreateUserDto dto);

    @Mapping(target = "roles", source = "roles")
    UserDto entityToDto(User user);

    default String mapRoleToString(Role role) {
        return role.getRoleName();
    }
}
