package com.re.rikkeibanking.service;

import com.re.rikkeibanking.dto.request.LoginRequest;
import com.re.rikkeibanking.dto.request.ForgotPasswordRequest;
import com.re.rikkeibanking.dto.request.ResetPasswordRequest;
import com.re.rikkeibanking.dto.request.RegisterRequest;
import com.re.rikkeibanking.dto.response.ForgotPasswordResponse;
import com.re.rikkeibanking.dto.response.LoginResponse;
import com.re.rikkeibanking.entity.RefreshToken;
import com.re.rikkeibanking.entity.TokenBlackList;
import com.re.rikkeibanking.entity.User;
import com.re.rikkeibanking.entity.Role;
import com.re.rikkeibanking.entity.PasswordResetToken;
import com.re.rikkeibanking.exception.BusinessException;
import com.re.rikkeibanking.repository.TokenBlackListRepository;
import com.re.rikkeibanking.repository.UserRepository;
import com.re.rikkeibanking.repository.RoleRepository;
import com.re.rikkeibanking.repository.PasswordResetTokenRepository;
import com.re.rikkeibanking.security.JwtService;
import com.re.rikkeibanking.security.UserPrincipal;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlackListRepository tokenBlackListRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(()->new BusinessException("User not found"));

        RefreshToken refreshToken =  refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtService.getExpirationSeconds(),
                refreshToken.getToken()
        );
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists", HttpStatus.BAD_REQUEST);
        }

        Role role = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new BusinessException("Role not found", HttpStatus.INTERNAL_SERVER_ERROR));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setIsActive(true);
        user.setIsKyc(false);
        user.setRole(role);

        userRepository.save(user);
    }

    @Transactional
    public void logOut(String authorizationHeader, String refreshToken){
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            throw new BusinessException("Missing bearer token", HttpStatus.UNAUTHORIZED);
        }

        String token = authorizationHeader.substring(7);

        // Bước 1: Xử lý accessToken - blacklist nếu còn hạn
        try {
            String userName = jwtService.extractUsername(token);
            log.info("[LOGOUT] Attempting logout for user: {}", userName);

            User user = userRepository.findByUsername(userName)
                    .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

            LocalDateTime expiryAt = jwtService.extractExpiration(token)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // Blacklist accessToken TRƯỚC
            if (!tokenBlackListRepository.existsByAccessToken(token)) {
                TokenBlackList tokenBlackList = new TokenBlackList();
                tokenBlackList.setAccessToken(token);
                tokenBlackList.setExpiryAt(expiryAt);
                tokenBlackList.setUser(user);
                tokenBlackListRepository.save(tokenBlackList);
                log.info("[LOGOUT] Access token blacklisted for user: {}", userName);
            } else {
                log.info("[LOGOUT] Access token already in blacklist for user: {}", userName);
            }

            // Kiểm tra ownership của refreshToken
            RefreshToken refreshToken1 = refreshTokenService.verifyRefreshToken(refreshToken);
            if(!refreshToken1.getUser().getId().equals(user.getId())){
                throw new BusinessException("Refresh token does not belong to current user", HttpStatus.UNAUTHORIZED);
            }

        } catch (ExpiredJwtException exception) {
            // Access token đã hết hạn → không cần blacklist
            log.info("[LOGOUT] Access token already expired, skipping blacklist");
        } catch (BusinessException e) {
            throw e;
        }

        // Bước 2: Luôn revoke refreshToken dù accessToken có hết hạn hay không
        refreshTokenService.revokeRefreshToken(refreshToken);
        log.info("[LOGOUT] Refresh token revoked successfully");
    }

    public LoginResponse refreshToken(String refreshTokenValue){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenValue);
        User user = refreshToken.getUser();

        UserDetails userDetails = UserPrincipal.from(user);

        String accessToken = jwtService.generateToken(userDetails);

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtService.getExpirationSeconds(),
                refreshToken.getToken()
        );
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        String resetToken = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .map(user -> {
                    passwordResetTokenRepository.deleteByUserId(user.getId());
                    
                    String token = UUID.randomUUID().toString();
                    PasswordResetToken resetTokenEntity = new PasswordResetToken(
                            token, user, LocalDateTime.now().plusMinutes(15)
                    );
                    passwordResetTokenRepository.save(resetTokenEntity);
                    
                    emailService.sendPasswordResetEmail(user.getEmail(), token);
                    return token;
                })
                .orElse(null);

        return new ForgotPasswordResponse(
                "If the account exists, a password reset email has been sent",
                null, // Intentionally null for security in production
                15
        );
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("New password and confirmation password do not match", HttpStatus.BAD_REQUEST);
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getResetToken())
                .orElseThrow(() -> new BusinessException("Invalid or expired reset token", HttpStatus.UNAUTHORIZED));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new BusinessException("Invalid or expired reset token", HttpStatus.UNAUTHORIZED);
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        passwordResetTokenRepository.delete(resetToken);
    }
}
