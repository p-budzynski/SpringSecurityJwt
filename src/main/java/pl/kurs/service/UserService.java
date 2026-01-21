package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kurs.dto.CreateUserDto;
import pl.kurs.dto.UserDto;
import pl.kurs.entity.User;
import pl.kurs.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto createUser(CreateUserDto createUserDto) {
        User user = new User(
                createUserDto.getUserName(),
                createUserDto.getEmail(),
                passwordEncoder.encode(createUserDto.getPassword()),
                createUserDto.getRoles());

        User savedUser = userRepository.save(user);

        return new UserDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRoles());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
