package cl.bci.prueba.service;

import cl.bci.prueba.dto.GenericResponseDto;
import cl.bci.prueba.dto.PhoneDto;
import cl.bci.prueba.dto.UserDto;
import cl.bci.prueba.entity.PhoneEntity;
import cl.bci.prueba.entity.UserEntity;
import cl.bci.prueba.exception.GenericException;
import cl.bci.prueba.repository.PhoneRepository;
import cl.bci.prueba.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final PhoneRepository phoneRepository;
    private final DozerBeanMapper mapper;

    @Override
    public UserDto register(UserDto request) {
        if (userRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw GenericException
                    .newInstance(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .detail(String.format("Email %s ya se encuentra registrado.", request.getEmail()));
        }

        UserEntity userEntity = userRepository.save(mapper.map(request, UserEntity.class));
        List<PhoneDto> phoneList = savePhoneList(userEntity, request.getPhones());

        UserDto userCreated = mapper.map(userEntity, UserDto.class);
        userCreated.setPhones(phoneList);
        userCreated.setToken(authenticationService.generateAccessToken(userCreated.getEmail()));

        return userCreated;
    }

    private List<PhoneDto> savePhoneList(UserEntity userEntity, List<PhoneDto> phoneList) {
        if (phoneList == null || phoneList.isEmpty()) {
            return new ArrayList<>();
        }

        return phoneList
                .stream()
                .map(phoneDto -> mapper.map(phoneDto, PhoneEntity.class))
                .peek(phoneEntity -> phoneEntity.setUserEntity(userEntity))
                .map(phoneEntity -> mapper.map(phoneRepository.save(phoneEntity), PhoneDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(userEntity -> {
                    List<PhoneEntity> phoneList = phoneRepository.findAllByUserEntity(userEntity);
                    UserDto userDto = mapper.map(userEntity, UserDto.class);
                    userDto.setPhones(phoneList.stream().map(phoneEntity -> mapper.map(phoneEntity, PhoneDto.class)).collect(Collectors.toList()));
                    return userDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(UserDto request) {
        Optional<UserEntity> userEntityOpt = userRepository.findByEmailIgnoreCase(request.getEmail());

        if (!userEntityOpt.isPresent()) {
            throw GenericException
                    .newInstance(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .httpStatus(HttpStatus.NOT_FOUND)
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

    @Override
    public UserDto doLogin(String email, String password) {

        Optional<UserEntity> userEntityOpt = userRepository.findByEmailIgnoreCaseAndPassword(email, password);

        if (!userEntityOpt.isPresent()) {
            throw GenericException.newInstance("Bad_credentials")
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .detail("Email o contraseña incorrecto.");
        }

        userEntityOpt.get().setLastLogin(LocalDateTime.now());
        userRepository.save(userEntityOpt.get());

        List<PhoneEntity> phoneList = phoneRepository.findAllByUserEntity(userEntityOpt.get());

        UserDto userLoggedIn = mapper.map(userEntityOpt.get(), UserDto.class);
        userLoggedIn.setToken(authenticationService.generateAccessToken(userLoggedIn.getEmail()));
        userLoggedIn.setPhones(phoneList.stream().map(phoneEntity -> mapper.map(phoneEntity, PhoneDto.class)).collect(Collectors.toList()));

        return userLoggedIn;
    }

    @Override
    @Transactional
    public GenericResponseDto deleteUser(String email) {
        if (userRepository.deleteByEmailIgnoreCase(email) != 1) {
            throw GenericException
                    .newInstance(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .detail(String.format("Email %s no encontrado.", email));
        }

        return GenericResponseDto.builder()
                .message("Usuario eliminado con exito!")
                .details(Collections.singletonList(String.format("Usuario con email %s eliminado correctamente.", email)))
                .build();
    }

    private UserEntity mergeUserEntityWithUserDto(UserEntity userEntity, UserDto userDto) {
        if (userDto.getName() != null) {
            userEntity.setName(userDto.getName());
        }

        if (userDto.getPassword() != null) {
            userEntity.setPassword(userDto.getPassword());
        }

        return userEntity;
    }

    private List<PhoneEntity> mergePhoneEntities(List<PhoneEntity> phoneEntities, List<PhoneDto> phoneDtos, UserEntity userEntityNew) {
        if (phoneDtos == null || phoneDtos.isEmpty()) {
            return phoneEntities;
        }

        phoneDtos.forEach(phoneDto -> {
            Optional<PhoneEntity> phoneEntityMatch = phoneEntities.stream().filter(phoneEntity -> phoneEntity.getNumber().equals(phoneDto.getNumber())).findFirst();

            if (phoneEntityMatch.isPresent()) {
                phoneEntityMatch.get().setCityCode(phoneDto.getCityCode());
                phoneEntityMatch.get().setCountryCode(phoneDto.getCountryCode());
            } else {
                PhoneEntity newPhoneEntity = mapper.map(phoneDto, PhoneEntity.class);
                newPhoneEntity.setUserEntity(userEntityNew);
                phoneEntities.add(newPhoneEntity);
            }

        });

        return phoneEntities;
    }

}
