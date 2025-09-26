package com.akabazan.service;

import com.akabazan.repository.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByEmail(String email);
}
