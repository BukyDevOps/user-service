package buky.example.userservice.model;

import buky.example.userservice.model.enums.NotificationType;
import buky.example.userservice.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "tblUsers")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String email;

    private String name;

    private String surname;

    private String address;

    @Enumerated(EnumType.ORDINAL)
    private Role role;

    @ElementCollection(targetClass = NotificationType.class, fetch = FetchType.EAGER)
    @CollectionTable
    @Enumerated(EnumType.STRING)
    List<NotificationType> notificationTypes;

    private Integer ratingCount;

    private Double rating;

    private Boolean active;
}

