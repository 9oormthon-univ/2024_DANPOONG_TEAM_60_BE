package com.example.goo.Auth.controller;

import com.example.goo.Auth.entity.User;
import com.example.goo.Auth.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/token")
    public ResponseEntity<?> getAccessToken(
            @RegisteredOAuth2AuthorizedClient("kakao") OAuth2AuthorizedClient authorizedClient) {

        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        return ResponseEntity.ok(Map.of("access_token", accessToken));
    }

    // 로그인 후 현재 사용자 정보 반환
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not authenticated"));
        }

        // 'id'를 Long으로 받아 처리
        Long oauthId = principal.getAttribute("id"); // 수정: Long 타입으로 처리
        User user = userRepository.findByOauthId(String.valueOf(oauthId)); // String 변환 후 사용

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not found"));
        }

        return ResponseEntity.ok(user);
    }

}
