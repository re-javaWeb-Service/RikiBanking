package com.re.rikkeibanking.repository;

import com.re.rikkeibanking.entity.KycProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KycProfileRepository extends JpaRepository<KycProfile,Long> {
}
