package cl.bci.prueba.controller;

import cl.bci.prueba.dto.UserDto;
import cl.bci.prueba.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthorizationController {

    private final UserService userService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserDto login(@RequestParam String email, @RequestParam String password) {
        return userService.doLogin(email, password);
    }

}
