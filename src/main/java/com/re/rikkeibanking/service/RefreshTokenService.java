package com.re.rikkeibanking.service;


import com.re.rikkeibanking.entity.RefreshToken;
import com.re.rikkeibanking.entity.User;
import com.re.rikkeibanking.exception.BusinessException;
import com.re.rikkeibanking.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
//repository dung de thao tac voi bảng refresh token
@Service
@RequiredArgsConstructor
public class RefreshTokenService  {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-token-expiration-seconds}")
    private Long refreshTokenExpirationSeconds;

    // tao refresh token khi user login thanh cong
    public RefreshToken createRefreshToken(User user){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(refreshTokenExpirationSeconds));
        refreshToken.setRevoked(false);
        return  refreshTokenRepository.save(refreshToken);
    }

    //THu hoi refresh token
    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    //check refresh token co hop le ko
    public RefreshToken verifyRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Invalid refresh token", HttpStatus.UNAUTHORIZED));        if (Boolean.TRUE.equals(refreshToken.getRevoked())){
            throw new BusinessException("Refresh token has been revoked", HttpStatus.UNAUTHORIZED);
        }

        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            throw new BusinessException("Refresh token has expired", HttpStatus.UNAUTHORIZED);        }
        return  refreshToken;
    }


}
