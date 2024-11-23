package com.example.goo.Post.controller;

import com.example.goo.Auth.entity.User;
import com.example.goo.Auth.repository.UserRepository;
import com.example.goo.Post.dto.CommentDto;
import com.example.goo.Post.dto.PostDto;
import com.example.goo.Post.entity.Post;
import com.example.goo.Post.repository.PostRepository;
import com.example.goo.Post.service.CommentService;
import com.example.goo.Post.service.PostService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@Slf4j
public class PostController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/post")
    public String post(Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
        String oauthid = Long.toString(oAuth2User.getAttribute("id"));
        User user = userRepository.findByOauthId(oauthid);
        List<Post> postEntityList = postRepository.findAll();
        model.addAttribute("postlist", postEntityList);
        if (user != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("user", user.getOauthId());
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "post/post";
    }
    @GetMapping("post/new")
    public String newPost(Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
        String oauthid = Long.toString(oAuth2User.getAttribute("id"));
        User user = userRepository.findByOauthId(oauthid);
        model.addAttribute("isLoggedIn", user != null);
        model.addAttribute("user", user != null ? user.getNickname() : null);
        return "post/postNew";
    }
    @PostMapping("/post/create")
    public String newPostForm(PostDto postDto, @AuthenticationPrincipal OAuth2User oAuth2User) {
        String oauthid = Long.toString(oAuth2User.getAttribute("id"));
        User user = userRepository.findByOauthId(oauthid);
        postDto.setNickname(user.getNickname());
        postDto.setOauthId(user.getOauthId());
        postDto.setUploadDate(LocalDate.now());
        Post post= postDto.toEntity();
        Post saved = postRepository.save(post);
        return "redirect:/post/" + saved.getOauthId() + "/" + saved.getId();
    }
    @GetMapping("/post/{oauthid}/{id}")
    public String showPost(@PathVariable String oauthid,
                            @PathVariable Long id, Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
        String findUser = Long.toString(oAuth2User.getAttribute("id"));
        User user = userRepository.findByOauthId(findUser);
        Post postEntity = postRepository.findByIdAndOauthId(id, oauthid);
        boolean isAuthor = user != null && user.getOauthId().equals(oauthid);
        if (postEntity != null) {
            postEntity.setViewCount(postEntity.getViewCount() + 1);
            postRepository.save(postEntity);
        }
        List<CommentDto> comments = commentService.comments(id);
        model.addAttribute("viewCount", postEntity.getViewCount());
        model.addAttribute("commentCount", postEntity.getCommentCount());
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("user", user != null ? user.getOauthId() : null);
        model.addAttribute("postlist", postEntity);
        model.addAttribute("isAuthor", isAuthor);
        model.addAttribute("comments", comments);
        return "post/postView";
    }
    @GetMapping("/post/{oauthid}/{id}/edit")
    public String edit(@PathVariable String oauthid, @PathVariable Long id, Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
        String findUser = Long.toString(oAuth2User.getAttribute("id"));
        User user = userRepository.findByOauthId(findUser);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("user", user.getNickname());
        Post post = postRepository.findByIdAndOauthId(id, oauthid);

        if (post == null) {
            return "redirect:/err";
        }
        model.addAttribute("editPost", post);
        return "post/postEdit";
    }
    @PostMapping("post/postUpdate")
    public String update(PostDto postDto, Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
        String findUser = Long.toString(oAuth2User.getAttribute("id"));
        User user = userRepository.findByOauthId(findUser);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("user", user.getNickname());
        Optional<Post> optionalPostEntity = postRepository.findById(postDto.getId());
        if (optionalPostEntity.isPresent()) {
            Post existingPost = optionalPostEntity.get();
            existingPost.setTitle(postDto.getTitle());
            existingPost.setContent(postDto.getContent());
            postRepository.save(existingPost);
        }
        return "redirect:/post/" + postDto.getOauthId() + "/" + postDto.getId();
    }
    @GetMapping("/post/{oauthid}/{id}/delete")
    public String Delete(@PathVariable Long id,
                         @PathVariable String oauthid, RedirectAttributes rttr,
                         Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
        String findUser = Long.toString(oAuth2User.getAttribute("id"));
        User user = userRepository.findByOauthId(findUser);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("user", user.getOauthId());
        postService.deletePost(id, oauthid);
        rttr.addFlashAttribute("msg", "삭제가 완료되었습니다!");
        return "redirect:/post";
    }
}