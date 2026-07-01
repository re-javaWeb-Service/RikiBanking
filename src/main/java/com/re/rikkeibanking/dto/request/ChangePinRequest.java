package com.re.rikkeibanking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePinRequest {
    @NotBlank
    private String oldPin;

    @NotBlank
    @Pattern(regexp = "^[0-9]{6}$", message = "New PIN must contain exactly 6 digits")
    private String newPin;

    @NotBlank
    private String confirmNewPin;
}
