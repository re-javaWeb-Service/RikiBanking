package com.re.rikkeibanking.service;

import com.re.rikkeibanking.aspect.LogAudit;
import com.re.rikkeibanking.dto.response.KycProfileResponseDto;
import com.re.rikkeibanking.entity.KycProfile;
import com.re.rikkeibanking.entity.User;
import com.re.rikkeibanking.exception.BusinessException;
import com.re.rikkeibanking.repository.KycProfileRepository;
import com.re.rikkeibanking.repository.UserRepository;
import com.re.rikkeibanking.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class KycService {
    private final KycProfileRepository kycProfileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    @LogAudit("KYC_UPLOAD")
    public KycProfileResponseDto upload(
            Authentication authentication,
            String fullName,
            String idNumber,
            LocalDate dob,
            String sex,
            String address,
            MultipartFile idCardFront,
            MultipartFile idCardBack
    ) {
        Long userId = currentUserId(authentication);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        KycProfile profile = kycProfileRepository.findByUserId(userId).orElseGet(KycProfile::new);
        profile.setUser(user);
        profile.setFullName(fullName);
        profile.setIdNumber(idNumber);
        profile.setDob(dob);
        profile.setSex(sex);
        profile.setAddress(address);
        profile.setIdCardFrontUrl(fileStorageService.uploadKycFile(idCardFront));
        if (idCardBack != null && !idCardBack.isEmpty()) {
            profile.setIdCardBackUrl(fileStorageService.uploadKycFile(idCardBack));
        }
        profile.setStatus("PENDING");
        profile.setVerifiedAt(null);
        user.setIsKyc(false);

        return toResponse(kycProfileRepository.save(profile));
    }

    public Page<KycProfileResponseDto> getProfiles(String status, Pageable pageable) {
        Page<KycProfile> profiles = status == null || status.isBlank()
                ? kycProfileRepository.findAll(pageable)
                : kycProfileRepository.findByStatus(status, pageable);
        return profiles.map(this::toResponse);
    }

    public KycProfileResponseDto getProfile(Long id) {
        return toResponse(findProfile(id));
    }

    public KycProfileResponseDto getMyProfile(Authentication authentication) {
        Long userId = currentUserId(authentication);
        KycProfile profile = kycProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("KYC profile not found", HttpStatus.NOT_FOUND));
        return toResponse(profile);
    }

    @Transactional
    @LogAudit("KYC_APPROVE")
    public KycProfileResponseDto approve(Long id) {
        KycProfile profile = findProfile(id);
        profile.setStatus("CONFIRM");
        profile.setVerifiedAt(LocalDateTime.now());
        profile.getUser().setIsKyc(true);
        return toResponse(profile);
    }

    @Transactional
    @LogAudit("KYC_REJECT")
    public KycProfileResponseDto reject(Long id) {
        KycProfile profile = findProfile(id);
        profile.setStatus("REJECT");
        profile.setVerifiedAt(null);
        profile.getUser().setIsKyc(false);
        return toResponse(profile);
    }

    private KycProfile findProfile(Long id) {
        return kycProfileRepository.findWithUserById(id)
                .orElseThrow(() -> new BusinessException("KYC profile not found", HttpStatus.NOT_FOUND));
    }

    private Long currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        throw new BusinessException("Invalid authenticated user", HttpStatus.UNAUTHORIZED);
    }

    private KycProfileResponseDto toResponse(KycProfile profile) {
        return new KycProfileResponseDto(
                profile.getId(),
                profile.getUser().getId(),
                profile.getFullName(),
                profile.getIdNumber(),
                profile.getDob(),
                profile.getSex(),
                profile.getAddress(),
                profile.getIdCardFrontUrl(),
                profile.getIdCardBackUrl(),
                profile.getStatus(),
                profile.getVerifiedAt(),
                profile.getCreatedAt()
        );
    }
}
