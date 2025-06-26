
package io.villapms.villapms.dto;

import io.villapms.villapms.model.Notification.NotificationType;
import lombok.Data;

@Data
public class NotificationUpdateDto {
    private String title;
    private String description;
    private Boolean isRead;
    private NotificationType type;
}