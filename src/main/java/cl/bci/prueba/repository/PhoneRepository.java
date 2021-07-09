package cl.bci.prueba.repository;

import cl.bci.prueba.entity.PhoneEntity;
import cl.bci.prueba.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhoneRepository extends CrudRepository<PhoneEntity, UUID> {

    List<PhoneEntity> findAll();

    List<PhoneEntity> findAllByUserEntity(UserEntity userEntity);

}
