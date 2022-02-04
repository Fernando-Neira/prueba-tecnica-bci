package cl.bci.prueba.repository;

import cl.bci.prueba.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de usuarios
 */
@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {

    /**
     * Método que obtiene los usuarios en una Lista
     * @return
     */
    List<UserEntity> findAll();

    /**
     * Metodo que busca un usuario por su email
     * @param email
     * @return Optional
     */
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    /**
     * Método que obtiene un usuario por su email y contraseña
     * @param email
     * @param password
     * @return Optional
     */
    Optional<UserEntity> findByEmailIgnoreCaseAndPassword(String email, String password);

    /**
     * Método que elimina un usuario por su email
     * @param email
     * @return
     */
    int deleteByEmailIgnoreCase(String email);


}
