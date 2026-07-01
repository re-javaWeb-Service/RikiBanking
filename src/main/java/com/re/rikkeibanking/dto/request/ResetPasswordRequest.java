package com.re.rikkeibanking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotBlank
    private String resetToken;

    @NotBlank
    @Size(min = 6, message = "New password must contain at least 6 characters")
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}
