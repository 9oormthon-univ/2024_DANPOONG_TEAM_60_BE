package com.example.goo.Post.service;

import com.example.goo.Auth.entity.User;
import com.example.goo.Auth.repository.UserRepository;
import com.example.goo.Post.dto.CommentDto;
import com.example.goo.Post.entity.Comment;
import com.example.goo.Post.entity.Post;
import com.example.goo.Post.repository.CommentRepository;
import com.example.goo.Post.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }
    public CommentDto findById(Long id) {
        return comments(id).stream()
                .findFirst()
                .orElse(null);
    }
    @Transactional
    public void addComment(String commentText, Long id, User sessionUser) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        User user = userRepository.findByOauthId(sessionUser.getOauthId());
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 포스트를 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .comment(commentText)
                .user(user)
                .post(post)
                .build();
        commentRepository.save(comment);

        Post postEntity = postRepository.findById(id).orElse(null);
        if (postEntity != null) {
            postEntity.setCommentCount(postEntity.getCommentCount() + 1);
            postRepository.save(postEntity);
        }
    }
    public List<CommentDto> comments(Long postId) {
        return commentRepository.findByPost_Id(postId).stream()
                .map(comment -> {
                    return new CommentDto(
                            comment.getComment(),
                            comment.getUser().getOauthId()
                    );
                })
                .collect(Collectors.toList());
    }

}