package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kurs.dto.CreateUserDto;
import pl.kurs.dto.UserDto;
import pl.kurs.entity.Role;
import pl.kurs.entity.User;
import pl.kurs.exception.UserNotFoundException;
import pl.kurs.mapper.UserMapper;
import pl.kurs.repository.UserRepository;

import javax.management.relation.RoleNotFoundException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private UserMapper userMapperMock;

    @Mock
    private PasswordEncoder passwordEncoderMock;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserSuccessfully() {
        //given
        Long id = 1L;
        CreateUserDto createUserDto = new CreateUserDto("username", "username@test.com", "password");
        String hashedPassword = "encoded_secret_123";

        User user = new User("username", "username@test.com", "password");

        User savedUser = new User("username", "username@test.com", hashedPassword);
        savedUser.setId(id);

        UserDto expectedUserDto = new UserDto(id, "username", "username@test.com", Set.of());

        when(userMapperMock.dtoToEntity(createUserDto)).thenReturn(user);
        when(passwordEncoderMock.encode(createUserDto.getPassword())).thenReturn(hashedPassword);

        when(userRepositoryMock.save(any(User.class))).thenReturn(savedUser);
        when(userMapperMock.entityToDto(savedUser)).thenReturn(expectedUserDto);

        //when
        UserDto result = userService.createUser(createUserDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedUserDto);

        verify(userRepositoryMock).save(argThat(u -> u.getPassword().equals(hashedPassword)));
    }

//    @Test
//    void shouldReturnUserDetailsWhenFindByUsername() {
//        //given
//        String username = "userTest";
//        User user = new User(username, "user@test.com", "hashed_password");
//
//        when(userRepositoryMock.findByUsernameWithRoles(username)).thenReturn(Optional.of(user));
//
//        //when
//        UserDetails result = userService.loadUserByUsername(username);
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getUsername()).isEqualTo(username);
//    }

//    @Test
//    void shouldThrowExceptionWhenUsernameNotFound() {
//        //given
//        String username = "nonExistent";
//
//        when(userRepositoryMock.findByUsernameWithRoles(username)).thenReturn(Optional.empty());
//
//        //when then
//        assertThatThrownBy(() -> userService.loadUserByUsername(username))
//                .isInstanceOf(UsernameNotFoundException.class)
//                .hasMessage("User: " + username + " not found");
//    }

    @Test
    void shouldReturnUserWhenFindById() {
        //given
        Long userId = 1L;
        User user = new User("userTest", "user@test.com", "hashed_password");
        user.setId(userId);

        when(userRepositoryMock.findByIdWithRoles(userId)).thenReturn(Optional.of(user));

        //when
        User result = userService.getUserByIdWithRoles(userId);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(user);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundById() {
        //given
        Long id = 1L;

        when(userRepositoryMock.findByIdWithRoles(id)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> userService.getUserByIdWithRoles(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with id: " + id + " not found");
    }

    @Test
    void shouldDeleteUserById() {
        //given
        Long id = 1L;

        //when
        userService.deleteUserById(id);

        //then
        verify(userRepositoryMock, times(1)).deleteById(id);
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        //given
        Long id = 1L;
        User existingUser = new User("userTest", "user@test.com", "hashed_password");
        existingUser.setId(id);

        User newUser = new User("newUserName", "new@test.com", "hashed_password");

        CreateUserDto createUserDto = new CreateUserDto("newUserName", "new@test.com", "hashed_password");
        UserDto userDto = new UserDto(1L, "newUserName", "new@test.com", Set.of());

        when(userRepositoryMock.findByIdWithRoles(id)).thenReturn(Optional.of(existingUser));
        when(userMapperMock.dtoToEntity(createUserDto)).thenReturn(newUser);
        when(userRepositoryMock.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userMapperMock.entityToDto(any(User.class))).thenReturn(userDto);

        //given
        UserDto result = userService.updateUser(id, createUserDto);

        //then
        assertThat(result.getUsername()).isEqualTo(newUser.getUsername());
        assertThat(result.getEmail()).isEqualTo(newUser.getEmail());
        verify(userRepositoryMock).save(existingUser);
    }

    @Test
    void shouldAssignRoleToUser() throws RoleNotFoundException {
        //given
        Long id = 1L;
        String roleName = "ROLE_ADMIN";
        User user = new User("userTest", "user@test.com", "password");
        Role role = Role.ROLE_ADMIN;

        when(userRepositoryMock.findByIdWithRoles(id)).thenReturn(Optional.of(user));


        //when
        userService.assignRoleToUser(id, roleName);

        //then
        assertThat(user.getRoles()).hasSize(1);
        assertThat(user.getRoles()).contains(role);
    }

    @Test
    void shouldNotAssignRoleIfUserHasTheSame() throws RoleNotFoundException {
        //given
        Long id = 1L;
        String roleName = "ROLE_ADMIN";
        Role role = Role.ROLE_ADMIN;
        User user = new User("userTest", "user@test.com", "password");
        user.getRoles().add(role);

        when(userRepositoryMock.findByIdWithRoles(id)).thenReturn(Optional.of(user));

        //when
        userService.assignRoleToUser(id, roleName);

        //then
        assertThat(user.getRoles()).hasSize(1);
        assertThat(user.getRoles()).contains(role);
    }

    @Test
    void shouldRemoveRoleFromUser() throws RoleNotFoundException {
        //given
        Long id = 1L;
        String roleName = "ROLE_ADMIN";
        Role role = Role.ROLE_ADMIN;
        User user = new User("userTest", "user@test.com", "password");
        user.getRoles().add(role);

        when(userRepositoryMock.findByIdWithRoles(id)).thenReturn(Optional.of(user));

        //when
        userService.removeRoleFromUser(id, roleName);

        //then
        assertThat(user.getRoles()).hasSize(0);
        assertThat(user.getRoles()).doesNotContain(role);
    }

}