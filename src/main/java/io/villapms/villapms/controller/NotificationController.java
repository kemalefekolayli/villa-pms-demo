package io.villapms.villapms.controller;

import io.villapms.villapms.dto.NotificationCreateDto;
import io.villapms.villapms.dto.NotificationUpdateDto;
import io.villapms.villapms.model.Notification.Notification;
import io.villapms.villapms.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createNotification(@Valid @RequestBody NotificationCreateDto notificationDto) {
        try {
            Notification notification = notificationService.createNotification(notificationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Notification created successfully",
                    "notificationId", notification.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        try {
            Notification notification = notificationService.getNotificationById(id);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Boolean isRead) {
        List<Notification> notifications;

        if (userId != null) {
            notifications = notificationService.getNotificationsByUserId(userId);
        } else if (isRead != null) {
            notifications = notificationService.getNotificationsByReadStatus(isRead);
        } else {
            notifications = notificationService.getAllNotifications();
        }

        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateNotification(
            @PathVariable Long id,
            @Valid @RequestBody NotificationUpdateDto updateDto) {
        try {
            Notification updatedNotification = notificationService.updateNotification(id, updateDto);
            return ResponseEntity.ok(Map.of(
                    "message", "Notification updated successfully",
                    "notificationId", updatedNotification.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Notification deleted successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}/mark-read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Notification marked as read"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}