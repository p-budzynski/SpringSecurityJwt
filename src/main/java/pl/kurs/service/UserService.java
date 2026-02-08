package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.dto.CreateUserDto;
import pl.kurs.dto.UserDto;
import pl.kurs.entity.Role;
import pl.kurs.entity.User;
import pl.kurs.exception.ResourceAlreadyExistsException;
import pl.kurs.exception.UserNotFoundException;
import pl.kurs.mapper.UserMapper;
import pl.kurs.repository.UserRepository;

import javax.management.relation.RoleNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto createUser(CreateUserDto createUserDto) {
        if (userRepository.existsByUsername(createUserDto.getUsername())) {
            throw new ResourceAlreadyExistsException("Username is already taken");
        }
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is already registered");
        }

        User user = userMapper.dtoToEntity(createUserDto);
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        user.getRoles().add(Role.ROLE_GUEST);
        return userMapper.entityToDto(userRepository.save(user));
    }

    public User getUserByIdWithRoles(Long id) {
        return userRepository.findByIdWithRoles(id).
                orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public UserDto updateUser(Long id, CreateUserDto dto) throws UserNotFoundException {
        User userToUpdate = getUserByIdWithRoles(id);

        BeanUtils.copyProperties(userMapper.dtoToEntity(dto), userToUpdate, "id");

        return userMapper.entityToDto(userRepository.save(userToUpdate));
    }

    @Transactional
    public void assignRoleToUser(Long id, String roleName) throws RoleNotFoundException {
        User user = getUserByIdWithRoles(id);
        Role role = Role.fromString(roleName);

        user.getRoles().add(role);
    }

    @Transactional
    public void removeRoleFromUser(Long id, String roleName) throws RoleNotFoundException {
        User user = getUserByIdWithRoles(id);
        Role role = Role.fromString(roleName);

        user.getRoles().remove(role);
    }

}
