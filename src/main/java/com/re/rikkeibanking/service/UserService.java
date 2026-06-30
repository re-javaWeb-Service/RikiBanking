package com.re.rikkeibanking.service;

import com.re.rikkeibanking.dto.request.UserStatusRequest;
import com.re.rikkeibanking.dto.request.UserUpdateRequest;
import com.re.rikkeibanking.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponseDto> getUsers(Pageable pageable);

    UserResponseDto getUserById(Long id);

    UserResponseDto updateUser(Long id, UserUpdateRequest request);

    UserResponseDto updateStatus(Long id, UserStatusRequest request);
}
