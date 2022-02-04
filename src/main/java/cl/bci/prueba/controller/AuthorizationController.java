package cl.bci.prueba.controller;

import cl.bci.prueba.dto.UserDto;
import cl.bci.prueba.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de Autenticación
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthorizationController {

    /**
     * Bean de Servicio de usuario
     */
    private final UserService userService;

    /**
     * Endpoint de login
     * @param email Email
     * @param password Contraseña
     * @return UserDto en caso de login correcto, si no lanza una excepción
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserDto login(@RequestParam String email, @RequestParam String password) {
        return userService.doLogin(email, password);
    }

}
