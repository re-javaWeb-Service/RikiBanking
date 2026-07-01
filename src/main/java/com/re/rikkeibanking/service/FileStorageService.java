package com.re.rikkeibanking.service;

import com.re.rikkeibanking.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    public String uploadKycFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("KYC file must not be empty", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("KYC file must not exceed 5MB", HttpStatus.BAD_REQUEST);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BusinessException("KYC file type must be jpg, jpeg, png or pdf", HttpStatus.BAD_REQUEST);
        }
        String originalFilename = file.getOriginalFilename() == null ? "kyc-file" : file.getOriginalFilename();
        String safeFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        return "mock://kyc/" + UUID.randomUUID() + "/" + safeFilename;
    }
}
