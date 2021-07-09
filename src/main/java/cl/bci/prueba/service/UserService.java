package cl.bci.prueba.service;

import cl.bci.prueba.dto.GenericResponseDto;
import cl.bci.prueba.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto register(UserDto request);

    List<UserDto> getUsers();

    UserDto updateUser(UserDto request);

    UserDto doLogin(String email, String password);

    GenericResponseDto deleteUser(String email);
}
