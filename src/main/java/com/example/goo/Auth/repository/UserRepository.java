package com.example.goo.Auth.repository;

import com.example.goo.Auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByOauthId(String oauthId);

    Optional<User> findByProviderAndOauthId(String provider, String oauthId);
}
