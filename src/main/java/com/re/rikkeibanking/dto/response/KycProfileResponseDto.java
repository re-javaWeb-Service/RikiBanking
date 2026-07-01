package com.re.rikkeibanking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class KycProfileResponseDto {
    private Long id;
    private Long userId;
    private String fullName;
    private String idNumber;
    private LocalDate dob;
    private String sex;
    private String address;
    private String idCardFrontUrl;
    private String idCardBackUrl;
    private String status;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
}
