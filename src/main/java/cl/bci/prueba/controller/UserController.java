package cl.bci.prueba.controller;

import cl.bci.prueba.dto.GenericResponseDto;
import cl.bci.prueba.dto.ResponseListWrapper;
import cl.bci.prueba.dto.UserDto;
import cl.bci.prueba.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Controlador de usuarios
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * Bean de servicio de usuarios
     */
    private final UserService userService;

    /**
     * Endpoint de registro de usuario
     * @param request req
     * @return res
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto registerUser(@Valid @RequestBody UserDto request) {
        return userService.register(request);
    }

    /**
     * Endpoint para obtener todos los usuarios
     * @return
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseListWrapper<List<UserDto>> getUsers() {
        return new ResponseListWrapper<>(userService.getUsers());
    }

    /**
     * Endpoint para actualizar un usuario
     * @param request
     * @return
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@RequestBody UserDto request) {
        return userService.updateUser(request);
    }

    /**
     * Endpoint para eliminar un usuario
     * @param email
     * @return
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public GenericResponseDto deleteUser(@Valid @NotBlank(message = "Debes incluir el Email del usuario.") String email) {
        return userService.deleteUser(email);
    }

}
