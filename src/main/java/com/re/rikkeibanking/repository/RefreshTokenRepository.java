package com.re.rikkeibanking.repository;

import com.re.rikkeibanking.entity.RefreshToken;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(Long userId);
}
