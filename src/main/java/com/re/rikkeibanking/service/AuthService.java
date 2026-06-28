package com.re.rikkeibanking.service;


import com.re.rikkeibanking.dto.request.LoginRequest;
import com.re.rikkeibanking.dto.response.LoginResponse;
import com.re.rikkeibanking.entity.TokenBlackList;
import com.re.rikkeibanking.entity.User;
import com.re.rikkeibanking.repository.TokenBlackListRepository;
import com.re.rikkeibanking.repository.UserRepository;
import com.re.rikkeibanking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final TokenBlackListRepository tokenBlackListRepository;
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtService.getExpirationSeconds()
        );
    };

    public void logOut(String authorizationHeader){
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer")){
            throw new IllegalArgumentException("Missung bearer token");
        }

        String token = authorizationHeader.substring(7);
        String userName = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(userName).orElseThrow(()-> new IllegalArgumentException("User not found"));

        LocalDateTime expiryAt = jwtService.extractExpiration(token)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        if(!tokenBlackListRepository.existsByAccessToken(token)){
            TokenBlackList tokenBlackList = new TokenBlackList();
            tokenBlackList.setAccessToken(token);
            tokenBlackList.setExpiryAt(expiryAt);
            tokenBlackList.setUser(user);
            tokenBlackListRepository.save(tokenBlackList);
        }
    }


}
