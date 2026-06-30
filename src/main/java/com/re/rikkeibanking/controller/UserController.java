package com.re.rikkeibanking.controller;

import com.re.rikkeibanking.dto.request.UserStatusRequest;
import com.re.rikkeibanking.dto.request.UserUpdateRequest;
import com.re.rikkeibanking.dto.response.UserResponseDto;
import com.re.rikkeibanking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Page<UserResponseDto> getUsers(Pageable pageable){
        return userService.getUsers(pageable);
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return userService.updateUser(id, request);
    }

    @PatchMapping("/{id}/status")
    public UserResponseDto updateStatus(@PathVariable Long id, @Valid @RequestBody UserStatusRequest request){
        return userService.updateStatus(id,request);
    }


}
