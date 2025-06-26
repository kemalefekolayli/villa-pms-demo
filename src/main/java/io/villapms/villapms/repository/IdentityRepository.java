package io.villapms.villapms.repository;

import io.villapms.villapms.model.Identity.Identity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, Long> {
    List<Identity> findByUserId(Long userId);
    Optional<Identity> findByIdentityNumber(String identityNumber);
    Optional<Identity> findByPassportNumber(String passportNumber);
    boolean existsByIdentityNumber(String identityNumber);
    boolean existsByPassportNumber(String passportNumber);
}