
package io.villapms.villapms.service;

import io.villapms.villapms.dto.NotificationCreateDto;
import io.villapms.villapms.dto.NotificationUpdateDto;
import io.villapms.villapms.model.Notification.Notification;
import io.villapms.villapms.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(NotificationCreateDto notificationDto) {
        Notification notification = new Notification();
        notification.setTitle(notificationDto.getTitle());
        notification.setDescription(notificationDto.getDescription());
        notification.setUserId(notificationDto.getUserId());
        notification.setType(notificationDto.getType());
        notification.setIsRead(false);

        return notificationRepository.save(notification);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getNotificationsByReadStatus(Boolean isRead) {
        return notificationRepository.findByIsReadOrderByCreatedAtDesc(isRead);
    }

    public Notification updateNotification(Long id, NotificationUpdateDto updateDto) {
        Notification notification = getNotificationById(id);

        if (updateDto.getTitle() != null) {
            notification.setTitle(updateDto.getTitle());
        }
        if (updateDto.getDescription() != null) {
            notification.setDescription(updateDto.getDescription());
        }
        if (updateDto.getIsRead() != null) {
            notification.setIsRead(updateDto.getIsRead());
        }
        if (updateDto.getType() != null) {
            notification.setType(updateDto.getType());
        }

        return notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {
        Notification notification = getNotificationById(id);
        notificationRepository.delete(notification);
    }

    public void markAsRead(Long id) {
        Notification notification = getNotificationById(id);
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}