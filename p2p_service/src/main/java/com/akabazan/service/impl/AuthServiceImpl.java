package com.akabazan.service.impl;

import com.akabazan.repository.UserRepository;
import com.akabazan.repository.entity.User;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.common.constant.ErrorCode;
import com.akabazan.service.AuthService;
import com.akabazan.service.dto.AuthResult;
import com.akabazan.common.exception.ApplicationException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Locale;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SecretKey secretKey;

    public AuthServiceImpl(UserRepository userRepository, SecretKey secretKey) {
        this.userRepository = userRepository;
        this.secretKey = secretKey;
    }

    @Override
    public AuthResult login(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (!user.getPassword().equals(password)) {
            throw new ApplicationException(ErrorCode.INVALID_CREDENTIALS);
        }

        return toAuthResult(user, authenticateAndGenerateToken(user));
    }

    @Override
    @Transactional
    public AuthResult register(String email, String password) {
        String normalizedEmail = normalizeEmail(email);

        userRepository.findByEmail(normalizedEmail).ifPresent(existing -> {
            throw new ApplicationException(ErrorCode.USER_ALREADY_EXISTS);
        });

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(password);

        Wallet wallet = new Wallet();
        wallet.setToken("USDT");
        wallet.setBalance(100.0);
        wallet.setAvailableBalance(100.0);
        wallet.setUser(user);
        user.getWallets().add(wallet);

        User savedUser = userRepository.save(user);

        return toAuthResult(savedUser, authenticateAndGenerateToken(savedUser));
    }

    @Override
    public String issueToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        return authenticateAndGenerateToken(user);
    }

    private String authenticateAndGenerateToken(User user) {
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

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private AuthResult toAuthResult(User user, String token) {
        return new AuthResult(user.getId(), user.getEmail(), token);
    }
}
