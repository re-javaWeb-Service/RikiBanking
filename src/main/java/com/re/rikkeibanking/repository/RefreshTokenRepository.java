package com.re.rikkeibanking.repository;

import com.re.rikkeibanking.entity.RefreshToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    @EntityGraph(attributePaths = {"user","user.role"})
    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(Long userId);
}
