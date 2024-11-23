package com.example.goo.Post.dto;

import com.example.goo.Post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDto {
    private long id;
    private String title;
    private String content;
    private String nickname;
    private String oauthId;
    private LocalDate uploadDate;
    private int viewCount;
    private int commentCount;

    public Post toEntity(){
        return new Post(id, title, content, nickname, oauthId, LocalDate.now(), viewCount, commentCount);
    }
}
