package com.re.rikkeibanking.service.impl;

import com.re.rikkeibanking.dto.request.UserStatusRequest;
import com.re.rikkeibanking.dto.request.UserUpdateRequest;
import com.re.rikkeibanking.dto.response.UserResponseDto;
import com.re.rikkeibanking.entity.Role;
import com.re.rikkeibanking.entity.User;
import com.re.rikkeibanking.exception.BusinessException;
import com.re.rikkeibanking.map.UserMapper;
import com.re.rikkeibanking.repository.RoleRepository;
import com.re.rikkeibanking.repository.UserRepository;
import com.re.rikkeibanking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Page<UserResponseDto> getUsers(Pageable pageable) {
        return userRepository.findUserPage(pageable);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        return userRepository.findUserResponseById(id)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
        if (request.getRole() != null) {
            Role role = roleRepository.findByName(request.getRole())
                    .orElseThrow(() -> new BusinessException("Role not found", HttpStatus.NOT_FOUND));
            user.setRole(role);
        }

        userRepository.save(user);
        return UserMapper.toResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateStatus(Long id, UserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        user.setIsActive(request.getIsActive());
        userRepository.save(user);

        return UserMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto getCurrentUserProfile(org.springframework.security.core.Authentication authentication) {
        if (authentication.getPrincipal() instanceof com.re.rikkeibanking.security.UserPrincipal userPrincipal) {
            return getUserById(userPrincipal.getId());
        }
        throw new BusinessException("Invalid authenticated user", HttpStatus.UNAUTHORIZED);
    }
}
