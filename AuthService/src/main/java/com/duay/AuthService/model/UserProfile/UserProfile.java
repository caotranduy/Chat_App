package com.duay.AuthService.model.UserProfile;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.duay.AuthService.model.UserAuthInfo.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
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
@Table(name = "user_profiles") 
public class UserProfile {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer profileId;


    @OneToOne(fetch = FetchType.LAZY) // Dùng Lazy loading để tránh tải User không cần thiết
    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false) 
    private User user; // Đối tượng User mà profile này thuộc về

    @Column
    private String displayName;

    @Lob
    private String bio;

    private String avatarUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    // public boolean isUserDeleted() {
        
    //     return user == null || user.isDeleted();
    // }

    public String getActualDisplayName() {
        if (displayName != null && !displayName.isEmpty()) {
            return displayName;
        }
        // Nếu user đã bị xóa (user.isDeleted() == true) hoặc user == null (do query @Where),
        // bạn có thể hiển thị một giá trị khác.
        if (user != null) {
            return user.getUsername();
        }
        return "Tài khoản đã bị xóa"; // Hoặc một thông báo phù hợp
    }
}

