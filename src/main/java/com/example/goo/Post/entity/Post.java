package com.example.goo.Post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column
    private String content;
    @Column
    private String nickname;
    @Column
    private String oauthId;
    @Column(name="uploadDate")
    @CreatedDate
    private LocalDate uploadDate;
    @Column(name="viewCount")
    private int viewCount=0;
    @Column
    private int commentCount=0;
}