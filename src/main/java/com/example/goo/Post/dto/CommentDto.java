package com.example.goo.Post.dto;

import com.example.goo.Auth.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private Long postId;
    private User user;
    private String comment;
    private String oauthid;

    public CommentDto(String comment, String oauthid) {
        this.comment = comment;
        this.oauthid = oauthid;
    }
}
