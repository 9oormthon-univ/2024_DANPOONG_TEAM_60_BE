package com.example.goo.Auth.service;

import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class FirebaseService {
    private static final String FIREBASE_CONFIG_PATH = "goorm-2dae7-firebase-adminsdk-tc5j0-7a4a2369b7.json";

    public String getAccessToken() {
        try {
            final GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream())
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            googleCredentials.refreshIfExpired();
            log.info("[ firebase ] token ---> {} ", googleCredentials.getAccessToken().getTokenValue());

            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            log.error("구글 토큰 요청 에러", e);
            return null;
        }
    }
}