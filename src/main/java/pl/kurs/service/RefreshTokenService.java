package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.entity.RefreshToken;
import pl.kurs.entity.User;
import pl.kurs.repository.RefreshTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshToken create(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES));
        return repository.save(refreshToken);
    }

    public User consume(UUID token) {
        RefreshToken refreshToken = repository.findById(token).orElseThrow(
                () -> new AuthenticationServiceException("Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthenticationServiceException("Refresh token expired");
        }

        repository.delete(refreshToken);

        return refreshToken.getUser();
    }

    public void delete(UUID token) {
        repository.deleteById(token);
    }

}
