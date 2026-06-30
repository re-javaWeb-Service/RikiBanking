package com.re.rikkeibanking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDto {

    private Long id;

    private String username;

    private String email;

    private String phoneNumber;

    private Boolean isActive;

    private Boolean isKyc;

    private String  role;

    private LocalDateTime createdAt;
}