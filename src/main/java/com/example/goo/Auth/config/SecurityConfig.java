package com.example.goo.Auth.config;

import com.example.goo.Auth.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;


import java.io.IOException;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("ed96b0600f58295622d71d0a86f7e1d6")
    private String clientId;

    @Value("http://44.212.10.165:8080")
    private String redirectUri;

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          OAuth2AuthorizedClientService authorizedClientService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.authorizedClientService = authorizedClientService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/login", "/api/logout", "/api/me").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            // OAuth2 인증 객체에서 AccessToken 가져오기
                            String accessToken = getAccessToken(authentication);

                            // HTTP 헤더에 AccessToken 추가
                            response.setHeader("Authorization", "Bearer " + accessToken);

                            // 로그인 성공 후 리다이렉션
                            response.sendRedirect("http://221.142.24.218:80/loginsuccess");
                        })
                        .failureHandler((request, response, exception) -> {
                            // 로그인 실패 시 React 로그인 실패 페이지로 리디렉션
                            response.sendRedirect("http://221.142.24.218:80/loginfailed");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessUrl("http://221.142.24.218:80/logoutsuccess") // 로그아웃 성공 후 React로 리디렉션
                        .addLogoutHandler((request, response, authentication) -> {
                            String logoutUrl = "https://kauth.kakao.com/oauth/logout?client_id=" + clientId + "&logout_redirect_uri=" + redirectUri;
                            try {
                                response.sendRedirect(logoutUrl);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        })
                )
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // CORS 명시적 활성화

        return http.build();
    }

    private String getAccessToken(Object authentication) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );
        return client.getAccessToken().getTokenValue();
    }

    // CORS 설정 추가
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://221.142.24.218:80")); // React 도메인
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 메서드
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // 허용할 헤더
        configuration.setAllowCredentials(true); // 세션 쿠키 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 엔드포인트에 대해 CORS 적용
        return source;
    }
}
