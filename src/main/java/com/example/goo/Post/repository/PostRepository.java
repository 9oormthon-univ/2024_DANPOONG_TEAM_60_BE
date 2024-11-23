package com.example.goo.Post.repository;

import com.example.goo.Post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Post findByIdAndOauthId(Long id, String oauthId);
}