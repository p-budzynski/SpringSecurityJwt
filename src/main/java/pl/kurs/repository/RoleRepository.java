package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByRoleName(String roleName);

    Optional<Role> findByRoleName(String roleName);
}
