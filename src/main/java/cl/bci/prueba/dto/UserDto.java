package cl.bci.prueba.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "id")
    private UUID userId;

    @NotBlank(message = "Debes ingresar un nombre.")
    private String name;

    @NotBlank(message = "Debes ingresar un email.")
    @Email(message = "Debes ingresar un email valido.")
    private String email;

    @NotBlank(message = "Debes ingresar una contraseña.")
    @Size(min = 8, message = "La contraseña debe contener al menos 8 caracteres.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "La contraseña debe contener una mayuscula, letras minúsculas, y dos numeros") //RegEx que valida que exista al menos una minuscula, una mayuscula, un digito y tenga un minimo de 8 caracteres
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime created;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime modified;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "last_login")
    private LocalDateTime lastLogin;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String token;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "is_active")
    private boolean isActive;

    private List<PhoneDto> phones;

}
