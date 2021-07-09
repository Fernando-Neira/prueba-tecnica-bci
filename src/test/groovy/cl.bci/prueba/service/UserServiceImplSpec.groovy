package cl.bci.prueba.service

import cl.bci.prueba.dto.PhoneDto
import cl.bci.prueba.dto.UserDto
import cl.bci.prueba.entity.PhoneEntity
import cl.bci.prueba.entity.UserEntity
import cl.bci.prueba.exception.GenericException
import cl.bci.prueba.repository.PhoneRepository
import cl.bci.prueba.repository.UserRepository
import org.dozer.DozerBeanMapper
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class UserServiceImplTest extends Specification {

    private UserRepository userRepository
    private PhoneRepository phoneRepository
    private DozerBeanMapper mapper
    private AuthenticationService authenticationService
    private UserServiceImpl userServiceImpl;

    def setup() {
        userRepository = Mock(UserRepository)
        phoneRepository = Mock(PhoneRepository)
        authenticationService = Mock(AuthenticationService)
        mapper = new DozerBeanMapper()
        mapper.setMappingFiles(Collections.singletonList("dozerJDK8.xml"));
        userServiceImpl = new UserServiceImpl(authenticationService, userRepository, phoneRepository, mapper)
    }

    def "registro usuario - caso error email ya registrado"() {
        given:
        userRepository.findByEmailIgnoreCase(_ as String) >> Optional.of(getUserEntity(1))
        UserDto userReq = UserDto.builder().email("fernando@neira.cl").build()

        when:
        userServiceImpl.register(userReq)

        then:
        def ex = thrown(GenericException)
        ex.getHttpStatus() == HttpStatus.BAD_REQUEST
        ex.getMessage() == HttpStatus.BAD_REQUEST.getReasonPhrase()
        ex.getDetails() != null
        !ex.getDetails().isEmpty()
        ex.getDetails().contains("Email fernando@neira.cl ya se encuentra registrado.")
    }

    def "registro usuario - caso exito sin telefonos"() {
        given:
        userRepository.findByEmailIgnoreCase(_ as String) >> Optional.empty()
        userRepository.save(_ as UserEntity) >> getUserEntity(1)
        UserDto userReq = getUserDto(1)

        when:
        def response = userServiceImpl.register(userReq)

        then:
        response.getEmail() == "user@uno.cl"
        response.getName() == "User uno"
        response.getPhones() != null
        response.getPhones().isEmpty()
    }

    def "registro usuario - caso exito con telefonos"() {
        given:
        userRepository.findByEmailIgnoreCase(_ as String) >> Optional.empty()
        userRepository.save(_ as UserEntity) >> getUserEntity(2)
        PhoneEntity phoneEntity1 = getPhoneEntityList(1).get(0)
        phoneEntity1.setUserEntity(getUserEntity(2));
        PhoneEntity phoneEntity2 = getPhoneEntityList(1).get(1)
        phoneEntity2.setUserEntity(getUserEntity(2));
        phoneRepository.save(phoneEntity1) >> phoneEntity1
        phoneRepository.save(phoneEntity2) >> phoneEntity2
        UserDto userReq = getUserDto(2)

        when:
        def response = userServiceImpl.register(userReq)

        then:
        response.getEmail() == "user@dos.cl"
        response.getName() == "User dos"
        response.getPhones() != null
        !response.getPhones().isEmpty()
        response.getPhones().get(0).getNumber() == "123456789"
        response.getPhones().get(1).getNumber() == "987654321"
    }

    def "obtener usuarios - caso exito"() {
        given:
        userRepository.findAll() >> Arrays.asList(getUserEntity(1), getUserEntity(2), getUserEntity(3))
        phoneRepository.findAllByUserEntity(getUserEntity(1)) >> getPhoneEntityList(1)
        phoneRepository.findAllByUserEntity(getUserEntity(2)) >> getPhoneEntityList(2)
        phoneRepository.findAllByUserEntity(getUserEntity(3)) >> getPhoneEntityList(3)


        when:
        def response = userServiceImpl.getUsers()

        then:
        response != null
        !response.isEmpty()
    }

    def "login - caso fallo credenciales"() {
        given:
        userRepository.findByEmailIgnoreCaseAndPassword(_ as String, _ as String) >> Optional.empty()
        UserDto userReq = getUserDto(1)

        when:
        userServiceImpl.doLogin(userReq.getEmail(), userReq.getPassword())

        then:
        def ex = thrown(GenericException)
        ex.getMessage() == "Bad_credentials"
        ex.getHttpStatus() == HttpStatus.FORBIDDEN
        ex.getDetails() != null
        !ex.getDetails().isEmpty()
        ex.getDetails().contains("Email o contraseña incorrecto.")
    }

    private UserDto getUserDto(int number) {
        switch (number) {
            case 1:
                return UserDto.builder()
                        .name("User uno")
                        .email("user@uno.cl")
                        .password("P4s5WorD")
                        .build();
            case 2:
                return UserDto.builder()
                        .name("User dos")
                        .email("user@dos.cl")
                        .password("PassW0Rd")
                        .phones(getPhoneDtoList(1))
                        .build();
            default:
                return UserDto.builder().build();
        }
    }

    private List<PhoneDto> getPhoneDtoList(int number) {
        switch (number) {
            case 1:
                return Arrays.asList(
                        PhoneDto.builder()
                                .number("123456789")
                                .cityCode("1")
                                .countryCode("100")
                                .build(),
                        PhoneDto.builder()
                                .number("987654321")
                                .cityCode("3")
                                .countryCode("144")
                                .build()
                )
            case 2:
                return Arrays.asList(
                        PhoneDto.builder()
                                .number("12345986")
                                .cityCode("2")
                                .countryCode("202")
                                .build()
                )
            case 3:
                return Arrays.asList(
                        PhoneDto.builder()
                                .number("987651234")
                                .cityCode("5")
                                .countryCode("303")
                                .build()
                )
        }
    }

    private List<PhoneEntity> getPhoneEntityList(int number) {
        switch (number) {
            case 1:
                return Arrays.asList(
                        PhoneEntity.builder()
                                .number("123456789")
                                .cityCode("1")
                                .countryCode("100")
                                .build(),
                        PhoneEntity.builder()
                                .number("987654321")
                                .cityCode("3")
                                .countryCode("144")
                                .build()
                )
            case 2:
                return Arrays.asList(
                        PhoneEntity.builder()
                                .number("12345986")
                                .cityCode("2")
                                .countryCode("202")
                                .build()
                )
            case 3:
                return Arrays.asList(
                        PhoneEntity.builder()
                                .number("987651234")
                                .cityCode("5")
                                .countryCode("303")
                                .build()
                )
        }
    }

    private UserEntity getUserEntity(int number) {
        switch (number) {
            case 1:
                return UserEntity.builder()
                        .name("User uno")
                        .email("user@uno.cl")
                        .password("P4s5WorD")
                        .build();
            case 2:
                return UserEntity.builder()
                        .name("User dos")
                        .email("user@dos.cl")
                        .password("PassW0Rd")
                        .build();
            default:
                return UserEntity.builder()
                        .name("User tres")
                        .email("user@tres.cl")
                        .password("P455W0Rd")
                        .build();
        }
    }

}
