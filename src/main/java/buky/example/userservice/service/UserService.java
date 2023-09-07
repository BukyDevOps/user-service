package buky.example.userservice.service;

import buky.example.userservice.exceptions.ActiveReservationExistsException;
import buky.example.userservice.exceptions.NotFoundException;
import buky.example.userservice.exceptions.UsernameExistsException;
import buky.example.userservice.messaging.KafkaProducer;
import buky.example.userservice.messaging.messages.*;
import buky.example.userservice.messaging.messages.enums.ReservationStatus;
import buky.example.userservice.model.User;
import buky.example.userservice.model.enums.NotificationType;
import buky.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducer publisher;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(String username) {
        System.out.println("Usao u deleteUser: username=" + username);
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        requestUserDeletion(UserDeletionRequestMessage
                .builder()
                .userId(user.getId())
                .userType(user.getRole())
                .build()
        );
    }

    public void performDeletion(UserDeletionResponseMessage message) {
        System.out.println("Primio poruku performDeletion: message.permitted=" + message.isPermitted());
        if(!message.isPermitted())
            throw new ActiveReservationExistsException("Deletion Now Allowed! User has active reservations!");

        //returns number of instances affected (1 if found, 0 otherwise)...
        userRepository.logicalDelete(message.getUserId());
    }

    public User registration(User user) {
        Optional<User> usr = userRepository.findUserByUsername(user.getUsername());

        if (usr.isPresent()) throw new UsernameExistsException("Username already exists!");

        user.setRatingCount(0);
        user.setRating(0.0);
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return saveUser(user);
    }

    public User updateUser(String username, User updatedUser) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException("User is not found!"));

        if (!username.equals(updatedUser.getUsername())
                && userRepository.findUserByUsername(updatedUser.getUsername()).isPresent())
            throw new UsernameExistsException("Username already exists!");

        if (!updatedUser.getPassword().equals(user.getPassword())) {
            updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        updatedUser.setRating(user.getRating());
        updatedUser.setRatingCount(user.getRatingCount());
        return userRepository.save(updatedUser);
    }

    public NotificationMessage updateHostRating(HostRatingMessage message) {
        User user = userRepository.findById(message.getHostId()).orElse(null);
        User guest = userRepository.findById(message.getUserId()).orElse(null);

        if (user == null || guest == null) return null;

        double newRating;

        if (message.getOldRatingValue() == 0) {
            newRating = (user.getRatingCount() * user.getRating() + message.getRatingValue())
                    / (user.getRatingCount() + 1);
            user.setRating(newRating);
            user.setRatingCount(user.getRatingCount() + 1);
        } else {
            newRating = (user.getRatingCount() * user.getRating() + message.getRatingValue() - message.getOldRatingValue())
                    / user.getRatingCount();
            user.setRating(newRating);
        }

        saveUser(user);

        return NotificationMessage.builder()
                .createdAt(LocalDateTime.now())
                .message(String.format("User: %s rated you with: %b", guest.getUsername(), message.getRatingValue()))
                .processed(!user.getNotificationTypes().contains(NotificationType.HOST_RATING))
                .notificationType(NotificationType.HOST_RATING)
                .receiverId(user.getId())
                .subjectId(user.getId())
                .build();
    }

    public NotificationMessage checkAccommodationRating(AccommodationRatingMessage message) {
        User user = userRepository.findById(message.getHostId()).orElse(null);
        User guest = userRepository.findById(message.getUserId()).orElse(null);

        if (user == null || guest == null) return null;

        return NotificationMessage.builder()
                .notificationType(NotificationType.ACCOMMODATION_RATING)
                .message("User: " + guest.getUsername() + "rated your accommodation with " + message.getRatingValue() + " stars!")
                .subjectId(message.getAccommodationId())
                .receiverId(message.getHostId())
                .processed(!user.getNotificationTypes().contains(NotificationType.ACCOMMODATION_RATING))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void requestUserDeletion(UserDeletionRequestMessage message) {
        System.out.println("saljem poruku iz requestUserDeletiona: userid="+message.getUserId());
        publisher.send("user-deletion-request-topic", message);
    }

    public NotificationMessage reservationStatusChanged(ReservationStatusChangedMessage message) {
        User receiver = userRepository.findById(message.getReceiverId()).orElse(null);
        User sender = userRepository.findById(message.getUserId()).orElse(null);

        if(receiver == null || sender == null) return null;

        return NotificationMessage.builder()
                .createdAt(LocalDateTime.now())
                .processed(!checkIfProcessed(receiver, message.getStatus()))
                .receiverId(message.getReceiverId())
                .notificationType(setNotificationType(message.getStatus()))
                .subjectId(message.getReservationId()).message(createMessage(sender, message.getStatus())).build();
    }

    private NotificationType setNotificationType(ReservationStatus status) {
        switch (status){
            case PENDING -> {return NotificationType.NEW_RESERVATION;}
            case CANCELED -> {return NotificationType.CANCELED_RESERVATION;}
            default -> {return NotificationType.PROCESSED_REQUEST;}
        }
    }

    private String createMessage(User sender, ReservationStatus status) {
        return String.format("User %s changed reservation status to: %s", sender.getUsername(), status.toString());
    }

    private Boolean checkIfProcessed(User receiver, ReservationStatus status) {
        if(status == ReservationStatus.CANCELED)
            return  receiver.getNotificationTypes().contains(NotificationType.CANCELED_RESERVATION);
        else if(status == ReservationStatus.PENDING)
            return receiver.getNotificationTypes().contains(NotificationType.NEW_RESERVATION);
        else
            return receiver.getNotificationTypes().contains(NotificationType.PROCESSED_REQUEST);
    }
}
