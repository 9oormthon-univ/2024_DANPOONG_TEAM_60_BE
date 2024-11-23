package com.example.goo.Auth.service;

import com.example.goo.Auth.entity.Fcm;
import com.example.goo.Auth.repository.FcmRepository;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PushNotificationService {
    private final FcmRepository fmcRepository;
    private final FcmSendService fcmService;

    public PushNotificationService(FcmRepository fmcRepository, FcmSendService fcmService) {
        this.fmcRepository = fmcRepository;
        this.fcmService = fcmService;
    }

    public void sendNotificationToAllUsers(String title, String body) {
        List<Fcm> users = fmcRepository.findAllByFcmTokenIsNotNull();
        for (Fcm user : users) {
            try {
                fcmService.sendNotificationToToken(user.getFcmToken(), title, body);
            } catch (FirebaseMessagingException e) {
                // 오류 처리
            }
        }
    }

    public void addToken(String token, String type) {
        Fcm fcm = new Fcm();
        fcm.setFcmToken(token);
        fcm.setType(type);
        fmcRepository.save(fcm);
    }

    public List<Fcm> viewAllTokens() {
        return fmcRepository.findAllByFcmTokenIsNotNull();
    }
}