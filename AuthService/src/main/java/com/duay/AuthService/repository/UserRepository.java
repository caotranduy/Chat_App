package com.duay.AuthService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duay.AuthService.model.User;

@Repository 
public interface UserRepository extends JpaRepository<User, Long> {
    // Find user by username
    Optional<User> findByUsername(String username);

    // Check if a user exists by username
    boolean existsByUsername(String username);
}