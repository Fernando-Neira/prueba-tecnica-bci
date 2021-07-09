package cl.bci.prueba.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponseDto {

    /**
     * Mensaje
     */
    private String message;

    /**
     * Detalles
     */
    private List<String> details;
}
