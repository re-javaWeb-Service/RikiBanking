package com.re.rikkeibanking.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountStatusRequest {
    @NotNull
    private Boolean active;
}
