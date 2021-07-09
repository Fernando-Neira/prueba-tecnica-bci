package cl.bci.prueba.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneEntity {

    @Id
    @Column(unique = true)
    private String number;

    @OneToOne
    private UserEntity userEntity;

    private String cityCode;
    private String countryCode;

}
