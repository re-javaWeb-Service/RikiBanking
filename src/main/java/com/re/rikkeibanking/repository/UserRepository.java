package com.re.rikkeibanking.repository;

import com.re.rikkeibanking.dto.response.UserResponseDto;
import com.re.rikkeibanking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @EntityGraph(attributePaths = "role")
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
        select new com.re.rikkeibanking.dto.response.UserResponseDto(
            u.id,
            u.username,
            u.email,
            u.phoneNumber,
            u.isActive,
            u.isKyc,
            u.role.name,
            u.createdAt
        )
        from User u
        """)
    Page<UserResponseDto> findUserPage(Pageable pageable);

    @Query("""
        select new com.re.rikkeibanking.dto.response.UserResponseDto(
            u.id,
            u.username,
            u.email,
            u.phoneNumber,
            u.isActive,
            u.isKyc,
            u.role.name,
            u.createdAt
        )
        from User u
        where u.id = :id
        """)
    Optional<UserResponseDto> findUserResponseById(Long id);
}
