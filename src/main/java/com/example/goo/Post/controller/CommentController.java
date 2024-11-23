package com.example.goo.Post.controller;

import com.example.goo.Auth.entity.User;
import com.example.goo.Auth.repository.UserRepository;
import com.example.goo.Post.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentController {
    @Autowired
    private UserRepository userRepository;
    private final CommentService commentService;
    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    @PostMapping("/{oauthid}/{id}/comment")
    public String addComment(@RequestParam("comment") String comment,
                             @PathVariable("oauthid") String oauthId,
                             @PathVariable("id") Long id,
                             @AuthenticationPrincipal OAuth2User oAuth2User) {
        String oauthid = Long.toString(oAuth2User.getAttribute("id"));
        User user = userRepository.findByOauthId(oauthid);
        if (user == null) {
            return "redirect:/err";
        }
        commentService.addComment(comment, id, user);
        return "redirect:/post/" + oauthId + "/" + id; // 닉네임 사용의 정확성 확인
    }
}