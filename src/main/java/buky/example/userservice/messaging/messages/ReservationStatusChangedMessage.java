package buky.example.userservice.messaging.messages;

import buky.example.userservice.messaging.messages.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationStatusChangedMessage implements Serializable {

    private Long userId;
    private Long receiverId;
    private Long reservationId;
    private ReservationStatus status;
}
