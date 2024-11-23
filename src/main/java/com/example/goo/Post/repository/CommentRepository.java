package com.example.goo.Post.repository;

import com.example.goo.Post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_Id(Long id);
    void deleteByPost_Id(Long postId);
}