package com.re.rikkeibanking.security;

import com.re.rikkeibanking.repository.TokenBlackListRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
//JwtAuthenticationFilter = kiểm tra token cho request
//Đọc Authorization header
//Lấy Bearer token
//Parse username từ token
//Load user từ database
//Validate token
//Set user vào SecurityContext
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService service;
    private final UserDetailsService userDetailsService;
    private final TokenBlackListRepository tokenBlackListRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
            ) throws ServletException, IOException{
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader== null || !authorizationHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        String token = authorizationHeader.substring(7);
//        Nếu token đã nằm trong blacklist
//-> không set Authentication vào SecurityContext
//                -> cho request đi tiếp
//                -> phía sau Spring Security thấy request chưa authenticated
//                -> endpoint cần login sẽ bị chặn
//        Điểm quan trọng là: nó không cho phép request, mà là cho request đi tiếp trong trạng thái chưa đăng nhập.

        if(tokenBlackListRepository.existsByAccessToken(token)){
            filterChain.doFilter(request,response);
            return;
        }
        String username = service.extractUsername(token);

        if(username != null && SecurityContextHolder.getContext().getAuthentication()== null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if(service.isTokenValid(token,userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);

    }
}
