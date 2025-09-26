package com.akabazan.service.impl;

import com.akabazan.repository.UserRepository;
import com.akabazan.repository.entity.User;
import com.akabazan.common.constant.ErrorCode;
import com.akabazan.service.AuthService;
import com.akabazan.common.exception.ApplicationException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SecretKey secretKey;

    public AuthServiceImpl(UserRepository userRepository, SecretKey secretKey) {
        this.userRepository = userRepository;
        this.secretKey = secretKey;
    }

    @Override
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (!user.getPassword().equals(password)) {
            throw new ApplicationException(ErrorCode.INVALID_CREDENTIALS);
        }

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user.getId().toString(), null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000L)) // 1 ngày
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
