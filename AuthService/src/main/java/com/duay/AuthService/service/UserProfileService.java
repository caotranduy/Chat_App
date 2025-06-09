package com.duay.AuthService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;                           
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duay.AuthService.dto.UserProfileResponse;
import com.duay.AuthService.exception.ResourceNotFoundException;
import com.duay.AuthService.model.UserAuthInfo.User;
import com.duay.AuthService.model.UserProfile.UserProfile;
import com.duay.AuthService.repository.UserCredentialRepository;
import com.duay.AuthService.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    
    private final UserProfileRepository userProfileRepository;
    private final UserCredentialRepository userCredentialRepository; 
    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    /**
     * Initialize a user profile for a given user ID at the time a new user is created
     * This method creates a new UserProfile instance with default values 
     * and associates it with the User entity.
     * @param userId
     * @return boolean indicating success or failure of the operation
     */
    @Transactional 
    public boolean initializeUserProfile(Long userId) {
       
        User existingUser = userCredentialRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Didn't find user with the given ID: " + userId));

       
        var userProfile = UserProfile.builder()
                .user(existingUser) 
                .displayName(existingUser.getUsername()) 
                .bio("")
                .avatarUrl("") 
                .build();
        try {
           
            userProfileRepository.save(userProfile);
        } catch (DataAccessException e) {
            logger.error("Error when creating user profile for user ID {}: {}", userId, e.getMessage());
            return false;
        }
        return true;
    }

    
 /**
     * Finds a user profile by username.
     * If found, it returns the profile data as a DTO.
     * If not found, it throws a ResourceNotFoundException.
     * @param username The username to search for.
     * @return UserProfileDTO containing public profile information.
     * @throws ResourceNotFoundException if no profile is found for the given username.
     */
    public UserProfileResponse getUserProfileByUsername(String username) {
        // Step 1: Query the repository.
        // The repository method performs the necessary JOIN.
        UserProfile userProfileEntity = userProfileRepository.findByUserUsername(username)
                // Step 2: If the Optional is empty, throw the correct, specific exception.
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for username: " + username));
        
        // Step 3: If found, map the entity to a DTO and return it.
        //return toUserProfileDTO(userProfileEntity);        
        return UserProfileResponse.builder()
                .username(userProfileEntity.getUser().getUsername())
                .displayName(userProfileEntity.getDisplayName())
                .bio(userProfileEntity.getBio())
                .avatarUrl(userProfileEntity.getAvatarUrl())
                .build();
    }

    /**
     * A private helper method to map the UserProfile entity to a UserProfileDTO.
     * This ensures no sensitive data is leaked.
     * * @param userProfile The entity fetched from the database.
     * @return A DTO safe to be sent to the client.
     */
    // private UserProfileResponse toUserProfileDTO(UserProfile userProfile) {


    //     // Create the final UserProfileResponse

    // }

    
}