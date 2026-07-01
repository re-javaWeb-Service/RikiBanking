package com.re.rikkeibanking.config;


import com.re.rikkeibanking.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(AbstractHttpConfigurer::disable)
                //Spring Security không lưu user login trong HTTP session nữa.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
//                        /api/auth/login   -> public, vì chưa đăng nhập vẫn phải login được
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh", "/api/auth/forgot-password", "/api/auth/reset-password").permitAll()
//                        /api/auth/refresh -> public, vì accessToken có thể hết hạn, dùng refreshToken lấy token mới
                        .requestMatchers("/api/auth/logout").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/users/*/status").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/v1/users/me").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF", "ROLE_CUSTOMER")
                                .requestMatchers("/api/v1/users/**").hasAnyAuthority("ROLE_ADMIN","ROLE_STAFF")
                                .requestMatchers(HttpMethod.GET, "/api/v1/audit-logs/**").hasAnyAuthority("ROLE_ADMIN","ROLE_STAFF")
                                .requestMatchers("/api/v1/kyc/upload").hasAuthority("ROLE_CUSTOMER")
                                .requestMatchers(HttpMethod.GET, "/api/v1/kyc/me").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF", "ROLE_CUSTOMER")
                                .requestMatchers(HttpMethod.GET, "/api/v1/kyc/**").hasAnyAuthority("ROLE_ADMIN","ROLE_STAFF")
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/kyc/**").hasAnyAuthority("ROLE_ADMIN","ROLE_STAFF")
                                .requestMatchers(HttpMethod.GET, "/api/v1/accounts/**").hasAnyAuthority("ROLE_ADMIN","ROLE_STAFF", "ROLE_CUSTOMER")
                                .requestMatchers(HttpMethod.POST, "/api/v1/accounts").hasAnyAuthority("ROLE_ADMIN","ROLE_STAFF")
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/accounts/*/pin").hasAuthority("ROLE_CUSTOMER")
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/accounts/**").hasAnyAuthority("ROLE_ADMIN","ROLE_STAFF")
                                .requestMatchers(HttpMethod.POST, "/api/v1/transactions/transfer").hasAuthority("ROLE_CUSTOMER")
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers ->
                        headers.frameOptions(frame -> frame.sameOrigin())
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());
         return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
            return configuration
                    .getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
    }
}
