package com.akabazan.service;


import com.akabazan.repository.entity.User;

import java.util.Optional;

public interface CurrentUserService {

    /**
     * Lấy User đang đăng nhập.
     *
     * @return Optional<User>, empty nếu chưa login
     */
    Optional<User> getCurrentUser();

    /**
     * Lấy ID của User đang đăng nhập.
     *
     * @return Optional<Long>, empty nếu chưa login
     */
    Optional<Long> getCurrentUserId();

    /**
     * Kiểm tra xem user hiện tại đã đăng nhập hay chưa
     *
     * @return true nếu user đang login
     */
    boolean isAuthenticated();
}