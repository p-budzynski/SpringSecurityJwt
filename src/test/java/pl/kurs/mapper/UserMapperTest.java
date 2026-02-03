package pl.kurs.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kurs.dto.CreateUserDto;
import pl.kurs.dto.UserDto;
import pl.kurs.entity.Role;
import pl.kurs.entity.User;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapDtoToEntityWithoutIdAndRoles() {
        //given
        User testUser = new User("testUser", "test@mail.com", "testPassword");
        CreateUserDto testUserDto = new CreateUserDto("testUser", "test@mail.com", "testPassword");

        //when
        User user = userMapper.dtoToEntity(testUserDto);

        //then
        assertThat(user)
                .usingRecursiveComparison()
                .ignoringFields("id", "roles")
                .isEqualTo(testUser);
        assertThat(user.getId()).isNull();
        assertThat(user.getRoles()).isEmpty();
    }

    @Test
    void shouldMapEntityToDtoWithRole() {
        //given
        User userTest = new User("testUser", "test@mail.com", "testPassword");
        userTest.setId(1L);
        userTest.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_MODERATOR, Role.ROLE_USER));

        UserDto testUserDto = new UserDto(1L, "testUser", "test@mail.com", Set.of("ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_USER"));

        //when
        UserDto dto = userMapper.entityToDto(userTest);

        //then
        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(testUserDto);
    }

    @Test
    void shouldMapEntityToDtoWithoutRoleWhenRoleIsNull() {
        //given
        User userTest = new User("testUser", "test@mail.com", "testPassword");
        userTest.setId(1L);
        userTest.setRoles(null);

        UserDto testUserDto = new UserDto(1L, "testUser", "test@mail.com", null);

        //when
        UserDto dto = userMapper.entityToDto(userTest);

        //then
        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(testUserDto);
    }

    @Test
    void shouldReturnNullWhenEntityToDtoGivenNull() {
        //when then
        assertThat(userMapper.entityToDto(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenDtoToEntityGivenNull() {
        //when then
        assertThat(userMapper.dtoToEntity(null)).isNull();
    }
}
