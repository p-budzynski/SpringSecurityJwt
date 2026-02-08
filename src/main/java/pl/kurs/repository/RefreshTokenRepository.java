package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.entity.RefreshToken;

import java.time.Instant;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken t WHERE t.expiresAt < :now")
    void deleteAllExpiredSince(Instant now);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken t WHERE t.user.id = :userId")
    void deleteAllByUserId(Long userId);
}
