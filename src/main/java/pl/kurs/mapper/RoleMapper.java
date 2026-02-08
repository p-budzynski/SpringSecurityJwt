package pl.kurs.mapper;

import org.mapstruct.Mapper;
import pl.kurs.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    default String map(Role role) {
        return role != null ? role.name() : null;
    }
}
