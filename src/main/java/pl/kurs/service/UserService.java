package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.dto.CreateUserDto;
import pl.kurs.dto.UserDto;
import pl.kurs.entity.Role;
import pl.kurs.entity.User;
import pl.kurs.exception.UserNotFoundException;
import pl.kurs.mapper.UserMapper;
import pl.kurs.repository.UserRepository;

import javax.management.relation.RoleNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto createUser(CreateUserDto createUserDto) {
        User user = userMapper.dtoToEntity(createUserDto);
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));

        return userMapper.entityToDto(userRepository.save(user));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User: " + username + " not found"));
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
        String normalizedName = ensureRolePrefix(roleName);
        Role role = Role.fromString(normalizedName);

        user.getRoles().add(role);
    }

    @Transactional
    public void removeRoleFromUser(Long id, String roleName) throws RoleNotFoundException {
        User user = getUserByIdWithRoles(id);
        String normalizedName = ensureRolePrefix(roleName);
        Role role = Role.fromString(normalizedName);

        user.getRoles().remove(role);
    }

    private String ensureRolePrefix(String roleName) {
        if (roleName == null) return "";

        String cleanName = roleName.trim().toUpperCase();
        return cleanName.startsWith("ROLE_") ? cleanName : "ROLE_" + cleanName;
    }
}
