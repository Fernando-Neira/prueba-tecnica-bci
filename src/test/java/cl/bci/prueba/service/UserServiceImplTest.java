package cl.bci.prueba.service;

import cl.bci.prueba.dto.PhoneDto;
import cl.bci.prueba.dto.UserDto;
import cl.bci.prueba.entity.UserEntity;
import cl.bci.prueba.repository.PhoneRepository;
import cl.bci.prueba.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dozer.DozerBeanMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserServiceImplTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PhoneRepository phoneRepository;

    @InjectMocks
    private DozerBeanMapper mapper = new DozerBeanMapper();

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper.setMappingFiles(Collections.singletonList("dozerJDK8.xml"));
    }

    @Test
    public void register_successful() {
        UserDto userDto = getUserDto(1);
        Mockito.when(userRepository.findByEmailIgnoreCase(Mockito.any())).thenReturn(Optional.empty());
        Mockito.doReturn(mapper.map(userDto, UserEntity.class)).when(userRepository).save(Mockito.any(UserEntity.class));

        UserDto userResponse = userServiceImpl.register(userDto);

        Assertions.assertEquals(userResponse.getEmail(), userDto.getEmail());
        Assertions.assertEquals(userResponse.getUserId(), userDto.getUserId());
        Assertions.assertEquals(userResponse.getName(), userDto.getName());
        Assertions.assertEquals(userResponse.getPhones(), userDto.getPhones());
        Assertions.assertEquals(userResponse.getCreated(), userDto.getCreated());


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
                return UserDto.builder().build();
            default:
                return UserDto.builder().build();
        }
    }

}
