package com.re.rikkeibanking.map;

import com.re.rikkeibanking.dto.response.UserResponseDto;
import com.re.rikkeibanking.entity.User;

public class UserMapper {
    private UserMapper(){}
    public static UserResponseDto toResponseDto(User user){
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getIsActive(),
                user.getIsKyc(),
                user.getRole().getName(),
                user.getCreatedAt()
        );
    }
}
