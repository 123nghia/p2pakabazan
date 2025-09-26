package com.akabazan.service;

public interface AuthService {
    /**
     * Xử lý login và trả về JWT token nếu thành công.
     *
     * @param email    Email đăng nhập
     * @param password Mật khẩu
     * @return JWT token
     */
    String login(String email, String password);
}
