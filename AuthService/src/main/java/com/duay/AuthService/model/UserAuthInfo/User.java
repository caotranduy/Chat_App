package com.duay.AuthService.model.UserAuthInfo;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
@Entity 
@Table(name = "user_credential") 
/*
 * User class implements UserDetails interface to provide user authentication and authorization information.
 * It contains fields for user ID, username, password, email, and role.
 */
public class User implements UserDetails {
    
    @Id 
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long userId;
    
    @Column(unique = true, nullable = false) 
    private String username;

    @Column(nullable = false) 
    private String password;

    @Column(unique = true, nullable = false) 
    private String email;

    @Enumerated(EnumType.STRING) 
    private Role role; 
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    // @Column(nullable = false, unique = true)
    // private String token;

    // @Column(nullable = false)
    // private Instant expiryDate;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() { return this.userId; }

}