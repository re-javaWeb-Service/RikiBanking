package com.re.rikkeibanking.repository;

import com.re.rikkeibanking.entity.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlackListRepository extends JpaRepository<TokenBlackList,Long> {

    boolean existsByAccessToken(String accessToken);

}
