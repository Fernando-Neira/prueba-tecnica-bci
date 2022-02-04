package cl.bci.prueba.service.user;

import cl.bci.prueba.dto.GenericResponseDto;
import cl.bci.prueba.dto.PhoneDto;
import cl.bci.prueba.dto.UserDto;
import cl.bci.prueba.entity.PhoneEntity;
import cl.bci.prueba.entity.UserEntity;
import cl.bci.prueba.exception.GenericException;
import cl.bci.prueba.repository.PhoneRepository;
import cl.bci.prueba.repository.UserRepository;
import cl.bci.prueba.service.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de usuario
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /**
     * Bean de servicio de autenticacion
     */
    private final AuthenticationService authenticationService;

    /**
     * Beand de repositorio de usuario
     */
    private final UserRepository userRepository;

    /**
     * Bean de repositorio de telefonos
     */
    private final PhoneRepository phoneRepository;

    /**
     * Bean de DozerBeanMapper
     */
    private final DozerBeanMapper mapper;

    /**
     * Método que registra a un usuario
     * @param request
     * @return
     */
    @Override
    public UserDto register(UserDto request) {
        if (userRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw new GenericException(HttpStatus.BAD_REQUEST).detail(String.format("Email %s ya se encuentra registrado.", request.getEmail()));
        }

        UserEntity userEntity = userRepository.save(mapper.map(request, UserEntity.class));
        List<PhoneDto> phoneDtosList = savePhoneList(userEntity, request.getPhones());

        UserDto userCreated = mapper.map(userEntity, UserDto.class);
        userCreated.setPhones(phoneDtosList);
        userCreated.setToken(authenticationService.generateAccessToken(userCreated.getEmail()));

        return userCreated;
    }

    /**
     * Método que guarda una lista de telefonos asociados a un UserEntity
     * @param userEntity
     * @param phoneList
     * @return
     */
    private List<PhoneDto> savePhoneList(UserEntity userEntity, List<PhoneDto> phoneList) {
        if (CollectionUtils.isEmpty(phoneList)) {
            return new ArrayList<>();
        }

        return phoneList
                .stream()
                .map(phoneDto -> mapper.map(phoneDto, PhoneEntity.class))
                .peek(phoneEntity -> phoneEntity.setUserEntity(userEntity))
                .map(phoneEntity -> {
                    PhoneEntity phoneSaved = phoneRepository.save(phoneEntity);
                    return mapper.map(phoneSaved, PhoneDto.class);
                })
                .collect(Collectors.toList());
    }

    /**
     * Método que obtiene los usuarios
     * @return
     */
    @Override
    public List<UserDto> getUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(userEntity -> {
                    List<PhoneDto> userPhones = getUserPhones(userEntity)
                            .stream()
                            .map(phoneEntity -> mapper.map(phoneEntity, PhoneDto.class))
                            .collect(Collectors.toList());

                    UserDto userDto = mapper.map(userEntity, UserDto.class);
                    userDto.setPhones(userPhones);
                    return userDto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Método que obtiene los telefonos de un usuario
     * @param userEntity
     * @return
     */
    private List<PhoneEntity> getUserPhones(UserEntity userEntity) {
        return phoneRepository.findAllByUserEntity(userEntity);
    }

    /**
     * Método que actualiza un usuario
     * @param request
     * @return
     */
    @Override
    public UserDto updateUser(UserDto request) {
        Optional<UserEntity> userEntityOpt = userRepository.findByEmailIgnoreCase(request.getEmail());

        if (!userEntityOpt.isPresent()) {
            throw new GenericException(HttpStatus.NOT_FOUND)
                    .detail(String.format("Email %s no encontrado.", request.getEmail()));
        }

        UserEntity userEntityNew = mergeUserEntityWithUserDto(userEntityOpt.get(), request);
        userEntityNew.setModified(LocalDateTime.now());
        userEntityNew = userRepository.save(userEntityNew);

        List<PhoneEntity> phoneEntityList = phoneRepository.findAllByUserEntity(userEntityNew);
        phoneEntityList = mergePhoneEntities(phoneEntityList, request.getPhones(), userEntityNew)
                .stream()
                .map(phoneRepository::save)
                .collect(Collectors.toList());

        UserDto userUpdated = mapper.map(userEntityNew, UserDto.class);
        userUpdated.setPhones(phoneEntityList.stream().map(phoneEntity -> mapper.map(phoneEntity, PhoneDto.class)).collect(Collectors.toList()));
        userUpdated.setToken(authenticationService.generateAccessToken(userUpdated.getEmail()));
        return userUpdated;
    }

    /**
     * Método que realiza login de un usuario
     * @param email
     * @param password
     * @return
     */
    @Override
    public UserDto doLogin(String email, String password) {

        Optional<UserEntity> userEntityOpt = userRepository.findByEmailIgnoreCaseAndPassword(email, password);

        if (!userEntityOpt.isPresent()) {
            throw new GenericException(HttpStatus.BAD_REQUEST).detail("Email o contraseña incorrectos.");
        }

        userEntityOpt.get().setLastLogin(LocalDateTime.now());
        userRepository.save(userEntityOpt.get());

        List<PhoneEntity> phoneList = phoneRepository.findAllByUserEntity(userEntityOpt.get());

        UserDto userLoggedIn = mapper.map(userEntityOpt.get(), UserDto.class);
        userLoggedIn.setToken(authenticationService.generateAccessToken(userLoggedIn.getEmail()));
        userLoggedIn.setPhones(phoneList.stream().map(phoneEntity -> mapper.map(phoneEntity, PhoneDto.class)).collect(Collectors.toList()));

        return userLoggedIn;
    }

    /**
     * Método que borra un usuario
     * @param email
     * @return
     */
    @Override
    @Transactional
    public GenericResponseDto deleteUser(String email) {
        UserEntity userEntity = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND).detail(String.format("Email %s no encontrado.", email)));

        if (phoneRepository.deleteAllByUserEntity(userEntity) != -1 && userRepository.deleteByEmailIgnoreCase(email) != 1) {
            throw new GenericException(HttpStatus.NOT_FOUND).detail(String.format("Error al eliminar usuario %s .", email));
        }

        return GenericResponseDto.builder()
                .message("Usuario eliminado con exito!")
                .details(Collections.singletonList(String.format("Usuario con email %s eliminado correctamente.", email)))
                .build();
    }

    /**
     * Método que realiza una mezcla de un UserEntity con un UserDto para actualizar campos que son editables (Nombre y Contraseña)
     * @param userEntity
     * @param userDto
     * @return
     */
    private UserEntity mergeUserEntityWithUserDto(UserEntity userEntity, UserDto userDto) {
        if (userDto.getName() != null) {
            userEntity.setName(userDto.getName());
        }

        if (userDto.getPassword() != null) {
            userEntity.setPassword(userDto.getPassword());
        }

        return userEntity;
    }

    /**
     * Método que realiza mezcla de dos listas de telefonos asociadas a un userEntity
     * @param phoneEntities
     * @param phoneDtos
     * @param newUserEntity
     * @return
     */
    private List<PhoneEntity> mergePhoneEntities(List<PhoneEntity> phoneEntities, List<PhoneDto> phoneDtos, UserEntity newUserEntity) {
        if (CollectionUtils.isEmpty(phoneDtos)) {
            return phoneEntities;
        }

        phoneDtos.forEach(phoneDto -> {
            Optional<PhoneEntity> phoneEntityMatch = phoneEntities.stream().filter(phoneEntity -> phoneEntity.getNumber().equals(phoneDto.getNumber())).findFirst();

            if (phoneEntityMatch.isPresent()) {
                phoneEntityMatch.get().setCityCode(phoneDto.getCityCode());
                phoneEntityMatch.get().setCountryCode(phoneDto.getCountryCode());
            } else {
                PhoneEntity newPhoneEntity = mapper.map(phoneDto, PhoneEntity.class);
                newPhoneEntity.setUserEntity(newUserEntity);
                phoneEntities.add(newPhoneEntity);
            }

        });

        return phoneEntities;
    }

}
