package com.example.goo.Auth.repository;

import com.example.goo.Auth.entity.Fcm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FcmRepository extends JpaRepository<Fcm, Long> {
    List<Fcm> findAllByFcmTokenIsNotNull();
}