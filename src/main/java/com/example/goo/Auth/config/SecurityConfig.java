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

    @Value("94d54435b2c08988febb12b2d5d13854")
    private String clientId;

    @Value("http://3.93.236.79 :8080")
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
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 명시적 활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/login", "/api/logout", "/api/me", "/notifications/add-token").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                                    ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId(),
                                    authentication.getName());

                            String accessToken = authorizedClient.getAccessToken().getTokenValue();
                            String redirectUrl = "http://localhost:3000/loginsuccess?access_token=" + accessToken;

                            response.sendRedirect(redirectUrl);
                        })
                        .failureHandler((request, response, exception) -> {
                            // 로그인 실패 시 React 로그인 실패 페이지로 리디렉션
                            response.sendRedirect("http://localhost:3000/loginfailed");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessUrl("http://localhost:3000/logoutsuccess") // 로그아웃 성공 후 React로 리디렉션
                        .addLogoutHandler((request, response, authentication) -> {
                            String logoutUrl = "https://kauth.kakao.com/oauth/logout?client_id=" + clientId + "&logout_redirect_uri=" + redirectUri;
                            try {
                                response.sendRedirect(logoutUrl);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        })
                );


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
        configuration.setAllowedOrigins(Arrays.asList("*")); // 모든 출처 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")); // 모든 HTTP 메서드 허용
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(false); // 인증 정보 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 엔드포인트에 CORS 설정 적용
        return source;
    }
}
