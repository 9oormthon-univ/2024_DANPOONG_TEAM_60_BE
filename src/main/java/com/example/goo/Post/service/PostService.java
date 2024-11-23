package com.example.goo.Post.service;

import com.example.goo.Post.entity.Post;
import com.example.goo.Post.repository.CommentRepository;
import com.example.goo.Post.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public void deletePost(Long id, String oauthId) {
        Post target = postRepository.findByIdAndOauthId(id, oauthId);
        if(target != null){
            commentRepository.deleteByPost_Id(target.getId());
            postRepository.delete(target);
        }
    }
}