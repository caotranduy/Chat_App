package com.duay.AuthService.service;

import java.util.List;
import java.util.stream.Collectors;

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
        // Improvement: Check if a profile already exists to prevent errors/duplicates.
        if (userProfileRepository.findByUserUserId(userId).isPresent()) {
            logger.warn("Attempted to initialize a profile that already exists for user ID: {}", userId);
            return true; // The goal is met, a profile exists.
        }

        // Improvement: Use the more semantically correct exception.
        User existingUser = userCredentialRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot initialize profile. User not found with ID: " + userId));

       
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
        UserProfile userProfileEntity = userProfileRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for username: " + username));
        
        // Improvement: Use a private helper method for mapping to keep the code clean.
        return toUserProfileResponse(userProfileEntity);
    }
    
    /**
     * A private helper method to map the UserProfile entity to a UserProfileResponse DTO.
     */
    private UserProfileResponse toUserProfileResponse(UserProfile userProfile) {
        return UserProfileResponse.builder()
                .username(userProfile.getUser().getUsername())
                .displayName(userProfile.getDisplayName())
                .bio(userProfile.getBio())
                .avatarUrl(userProfile.getAvatarUrl())
                // You can add other fields from UserProfile to the DTO here
                // For example: .profileId(userProfile.getProfileId())
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

    /**
     * Finds a user profile using a User entity object.
     * @param user The user entity.
     * @return A DTO of the user's profile.
     * @throws ResourceNotFoundException if no profile is found for the given user.
     */
    public UserProfileResponse getUserProfileByUser(User user) {
        UserProfile userProfileEntity = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for user: " + user.getUsername()));
        
        return toUserProfileResponse(userProfileEntity);
    }
    /**
     * Searches for user profiles where the username contains the given keyword.
     * @param keyword The keyword to search for.
     * @return A list of matching user profile DTOs.
     */
    public List<UserProfileResponse> searchProfilesByUsername(String keyword) {
        List<UserProfile> profiles = userProfileRepository.searchByUserUsernameContaining(keyword);
        
        // Map the list of entities to a list of DTOs
        return profiles.stream()
                .map(this::toUserProfileResponse)
                .collect(Collectors.toList());
    }
    
}