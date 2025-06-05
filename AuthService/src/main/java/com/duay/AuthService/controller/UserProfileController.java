package com.duay.AuthService.controller; 

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/user-profile")
@RequiredArgsConstructor
public class UserProfileController {
    @GetMapping("/")
    public String getUserProfile(@RequestParam String username) {
        // Giả sử bạn có một dịch vụ để lấy thông tin người dùng
        // Ở đây chỉ là ví dụ, bạn cần thay thế bằng logic thực tế
        return "Thông tin người dùng cho: " + username;
    } 
}
