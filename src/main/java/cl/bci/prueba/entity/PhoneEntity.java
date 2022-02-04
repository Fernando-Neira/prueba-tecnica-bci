package cl.bci.prueba.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;


/**
 * Clase de entidad de Telefono
 */
@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneEntity {

    /**
     * Identificador
     */
    @Id
    @Column(unique = true)
    private String number;

    /**
     * Relacion con usuario
     */
    @OneToOne
    private UserEntity userEntity;

    /**
     * Codigo de ciudad
     */
    private String cityCode;

    /**
     * Codigo de país
     */
    private String countryCode;

}
