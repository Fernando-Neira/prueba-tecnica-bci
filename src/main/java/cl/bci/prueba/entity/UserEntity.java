package cl.bci.prueba.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue
    private UUID userId;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private LocalDateTime created;
    private LocalDateTime modified;
    private LocalDateTime lastLogin;
    private boolean isActive;

    @PrePersist
    void preInsert() {
        if (this.created == null) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            this.created = currentDateTime;
            this.lastLogin = currentDateTime;
            this.isActive = true;
        }
    }

}
