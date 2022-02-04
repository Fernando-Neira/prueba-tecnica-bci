package cl.bci.prueba.repository;

import cl.bci.prueba.entity.PhoneEntity;
import cl.bci.prueba.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Clase repositorio de telefonos
 */
@Repository
public interface PhoneRepository extends CrudRepository<PhoneEntity, UUID> {

    /**
     * Método que obtiene todos los telefonos en una Lista
     * @return
     */
    List<PhoneEntity> findAll();

    /**
     * Metodo que obtiene todos los telefonos de un UserEntity
     * @param userEntity
     * @return
     */
    List<PhoneEntity> findAllByUserEntity(UserEntity userEntity);

    /**
     * Metodo que elimina los telefonos de un UserEntity
     * @param userEntity
     * @return
     */
    int deleteAllByUserEntity(UserEntity userEntity);

}
