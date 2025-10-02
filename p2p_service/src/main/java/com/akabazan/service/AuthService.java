package com.akabazan.service;

import com.akabazan.service.dto.AuthResult;

public interface AuthService {
    /**
     * Xử lý login và trả về JWT token nếu thành công.
     *
     * @param email    Email đăng nhập
     * @param password Mật khẩu
     * @return JWT token
     */
    AuthResult login(String email, String password);

    /**
     * Cấp JWT token trực tiếp cho user theo userId.
     * Dùng cho các flow tích hợp khi đã xác thực ở hệ thống ngoài.
     */
    String issueToken(Long userId);

    /**
     * Đăng ký user mới và trả về JWT token.
     */
    AuthResult register(String email, String password);
}
