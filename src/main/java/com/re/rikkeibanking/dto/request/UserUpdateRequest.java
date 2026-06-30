package com.re.rikkeibanking.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @Email(message = "Email khong hop le")
    private String email;

    @Pattern(regexp = "^[0-9]{9,11}$", message = "So dien thoai khong hop le")
    private String phoneNumber;

    private Boolean isActive;

    private String role;
}
