package com.duay.AuthService.model;

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

@Data // Tự động tạo getters, setters, toString, equals, hashCode (Lombok)
@Builder // Tự động tạo builder pattern (Lombok)
@NoArgsConstructor // Tự động tạo constructor không đối số (Lombok)
@AllArgsConstructor // Tự động tạo constructor với tất cả đối số (Lombok)
@Entity // Đánh dấu đây là một JPA Entity
@Table(name = "user_credential") // Đặt tên bảng trong cơ sở dữ liệu
public class User implements UserDetails {

    @Id // Đánh dấu khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng ID
    private Long id;

    @Column(unique = true, nullable = false) // Đảm bảo username là duy nhất và không null
    private String username;

    @Column(nullable = false) // Mật khẩu không null
    private String password;

    @Enumerated(EnumType.STRING) // Lưu trữ enum dưới dạng String trong DB
    private Role role; // Sử dụng Role enum cho vai trò

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

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
}