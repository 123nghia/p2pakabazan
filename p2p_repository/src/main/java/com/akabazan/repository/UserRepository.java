package com.akabazan.repository;
import com.akabazan.repository.entity.User;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.wallets WHERE u.id = :id")
    Optional<User> findByIdWithWallets(@Param("id") Long id);
}