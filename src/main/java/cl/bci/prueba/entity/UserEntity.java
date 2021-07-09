package cl.bci.prueba.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
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
