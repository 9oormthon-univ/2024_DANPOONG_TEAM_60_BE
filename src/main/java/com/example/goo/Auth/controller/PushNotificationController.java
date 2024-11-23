package com.example.goo.Auth.controller;

import com.example.goo.Auth.service.PushNotificationService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class PushNotificationController {
    private final PushNotificationService notificationService;

    public PushNotificationController(PushNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/send-to-all")
    public ResponseEntity<String> sendNotificationToAllUsers(@RequestParam String title, @RequestParam String body) {
        notificationService.sendNotificationToAllUsers(title, body);
        return ResponseEntity.ok("Notifications sent successfully");
    }

    @GetMapping("/add-token")
    public ResponseEntity<String> addToken(@RequestParam String token, @RequestParam String type) {
        notificationService.addToken(token, type);
        return ResponseEntity.ok("Token added successfully");
    }

    @GetMapping("/view-all-tokens")
    public ResponseEntity<?> viewAllTokens() {
        return ResponseEntity.ok(notificationService.viewAllTokens());
    }
}
