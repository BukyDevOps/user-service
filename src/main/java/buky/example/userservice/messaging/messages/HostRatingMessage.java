package buky.example.userservice.messaging.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostRatingMessage implements Serializable {

    private Long userId;
    private Long hostId;
    private Long ratingId;
    private Byte ratingValue;
    private Byte oldRatingValue;

}
