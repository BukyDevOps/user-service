package buky.example.userservice.messaging;

import buky.example.userservice.messaging.messages.AccommodationRatingMessage;
import buky.example.userservice.messaging.messages.HostRatingMessage;
import buky.example.userservice.messaging.messages.NotificationMessage;
import buky.example.userservice.messaging.messages.UserDeletionResponseMessage;
import buky.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final KafkaProducer producer;
    private final UserService userService;

    @KafkaListener(topics = "host-rating", containerFactory = "hostRatingListenerContainerFactory")
    public void hostRating(HostRatingMessage message) {
        NotificationMessage notificationMessage = userService.updateHostRating(message);

        if(notificationMessage != null) {
            producer.send("notifications-topic", notificationMessage);
        }

    }

    @KafkaListener(topics = "accommodation-rating",containerFactory = "accommodationRatingListenerContainerFactory")
    public void accommodationRating(AccommodationRatingMessage message) {
        NotificationMessage notificationMessage = userService.checkAccommodationRating(message);

        if(notificationMessage != null) {
            producer.send("notifications-topic", notificationMessage);
        }
    }

    @KafkaListener(topics = "user-deletion-permission-topic",containerFactory = "accommodationRatingListenerContainerFactory")
    public void userDeletionPermission(UserDeletionResponseMessage message) {
        userService.performDeletion(message);
    }
}