package cl.bci.prueba.controller;

import cl.bci.prueba.dto.GenericResponseDto;
import cl.bci.prueba.dto.UserDto;
import cl.bci.prueba.service.UserService;
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

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto registerUser(@Valid @RequestBody UserDto request) {
        return userService.register(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@RequestBody UserDto request) {
        return userService.updateUser(request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public GenericResponseDto deleteUser(@Valid @NotBlank(message = "Debes incluir el Email del usuario.") String email) {
        return userService.deleteUser(email);
    }

}
