
package io.villapms.villapms.dto;

import io.villapms.villapms.model.Notification.NotificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationCreateDto {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private Long userId;
    private NotificationType type = NotificationType.INFO;
}