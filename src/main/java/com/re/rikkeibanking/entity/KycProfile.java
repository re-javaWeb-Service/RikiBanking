package com.re.rikkeibanking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "kyc_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KycProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String idNumber;

    @Column(nullable = false, length = 100)
    private String fullName;

    private LocalDate dob;

    @Column(length = 20)
    private String sex;

    @Column(length = 255)
    private String address;

    //link url anh cccd truoc
    @Column(length = 500)
    private String idCardFrontUrl;
    //link url anh cccd sau
    @Column(length = 500)
    private String idCardBackUrl;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    private LocalDateTime verifiedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
