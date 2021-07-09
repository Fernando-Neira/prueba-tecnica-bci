package cl.bci.prueba.repository;

import cl.bci.prueba.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {

    List<UserEntity> findAll();

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    Optional<UserEntity> findByEmailIgnoreCaseAndPassword(String email, String password);

    int deleteByEmailIgnoreCase(String email);


}
