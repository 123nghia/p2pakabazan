package com.akabazan.repository;

import com.akabazan.repository.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByTypeAndRelId(String type, String relId);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.wallets WHERE u.id = :id")
    Optional<User> findByIdWithWallets(@Param("id") UUID id);

    List<User> findByRole(User.Role role);
}
