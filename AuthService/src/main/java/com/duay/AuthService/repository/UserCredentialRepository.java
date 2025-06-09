package com.duay.AuthService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duay.AuthService.model.UserAuthInfo.User;

@Repository 
public interface UserCredentialRepository extends JpaRepository<User, Long> {
    // Find user by username
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // Find user by userId
    Optional<User> findByUserId(Long userId);

    // Check if a user exists by username
    boolean existsByUsername(String username);
    // Check if a user exists by email
    boolean existsByEmail(String email);
 
}