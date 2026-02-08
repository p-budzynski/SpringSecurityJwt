package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.kurs.entity.UserLogin;
import pl.kurs.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserLoginService implements UserDetailsService {
private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameWithRoles(username)
                .map(UserLogin::new)
                .orElseThrow(() -> new UsernameNotFoundException("User: " + username + " not found"));
    }

}
