package com.re.rikkeibanking.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
//Lấy secret từ properties
//Convert secret thành SecretKey một lần
//Không convert lại mỗi lần generate token

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationSeconds;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration-seconds}") long expirationSeconds){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds=expirationSeconds;
        }
//1. Tạo token sau khi login thành công
        public String generateToken(UserDetails userDetails){
            Instant now = Instant.now();

            return Jwts.builder()
                    .subject(userDetails.getUsername())
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                    .signWith(secretKey)
                    .compact();

    }

    //2. Đọc username từ token
    public String extractUsername(String token) {
        return parseToken(token).getSubject();
    }

    //3. Kiểm tra token còn hạn không
    public Date extractExpiration(String token){
        return parseToken(token).getExpiration();
    }

    //4. Kiểm tra token có hợp lệ không
    public boolean isTokenValid(String token, UserDetails userDetails){
        String username = extractUsername(token);

        return username.equals(userDetails.getUsername()) &&
                        extractExpiration(token).after(new Date());
    }

    public long getExpirationSeconds(){
        return expirationSeconds;
    }

    private Claims parseToken(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
