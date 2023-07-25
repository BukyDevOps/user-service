package buky.example.userservice.model;

import buky.example.userservice.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
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

    private Boolean active;
}

