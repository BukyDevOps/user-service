package buky.example.userservice.messaging.messages;

import buky.example.userservice.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationMessage implements Serializable {
    private String id;
    private NotificationType notificationType;
    private Long subjectId;
    private Long receiverId;
    private String message;
    private LocalDateTime createdAt;
    private Boolean processed;
}
