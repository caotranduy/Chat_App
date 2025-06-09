package com.duay.AuthService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.duay.AuthService.model.UserProfile.UserProfile;

@Repository 
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    /**
     * Find User by JOINing the User table and filter by username
     * @param username of the User
     * @return an Optional of UserProfile if found, otherwise empty
     */
    @Query("SELECT p FROM UserProfile p JOIN p.user u WHERE u.username = :username")
    Optional<UserProfile> findByUserUsername(@Param("username") String username);

    /**
     * Find UserProfile by userId
     * @param userId of the User
     * @return an Optional of UserProfile if found, otherwise empty
     */
    Optional<UserProfile> findByUserUserId(Long userId);

    Optional<UserProfile> findByDisplayName(String displayName); 
    
}
