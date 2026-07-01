package com.re.rikkeibanking.service;

import com.re.rikkeibanking.dto.request.TransferRequest;
import com.re.rikkeibanking.dto.response.TransferResponse;
import org.springframework.security.core.Authentication;

public interface TransferService {
    TransferResponse transfer(TransferRequest request, Authentication authentication);
}
