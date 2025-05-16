package com.duay.AuthService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duay.AuthService.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
