package com.re.rikkeibanking.repository;

import com.re.rikkeibanking.entity.KycProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycProfileRepository extends JpaRepository<KycProfile,Long> {
    @EntityGraph(attributePaths = "user")
    Page<KycProfile> findByStatus(String status, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<KycProfile> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Optional<KycProfile> findWithUserById(Long id);

    @EntityGraph(attributePaths = "user")
    Optional<KycProfile> findByUserId(Long userId);
}
