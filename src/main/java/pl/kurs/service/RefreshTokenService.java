package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.entity.RefreshToken;
import pl.kurs.entity.User;
import pl.kurs.repository.RefreshTokenRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RefreshTokenService {

    @Value("${spring.security.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private final RefreshTokenRepository repository;
    private final UserService userService;

    public RefreshToken create(Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userService.getUserByIdWithRoles(userId));
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenExpirationMs));
        return repository.save(refreshToken);
    }

    public User consume(UUID token) {
        RefreshToken refreshToken = repository.findById(token).orElseThrow(
                () -> new AuthenticationServiceException("Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            repository.delete(refreshToken);
            throw new AuthenticationServiceException("Refresh token expired");
        }

        repository.delete(refreshToken);

        return refreshToken.getUser();
    }

    public void delete(UUID token) {
        repository.deleteById(token);
    }

    public void deleteAllByUserId(Long userId) {
        repository.deleteAllByUserId(userId);
    }

    @Scheduled(cron = "${spring.security.jwt.cleanup-cron}")
    public void purgeExpiredTokens() {
        log.info("Starting scheduled purge of expired refresh tokens");
        repository.deleteAllExpiredSince(Instant.now());
        log.info("Finished purging expired refresh tokens");
    }

}
