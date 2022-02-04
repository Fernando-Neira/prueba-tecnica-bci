package cl.bci.prueba.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Clase generica para envolver una lista en una respuesta y quede con el formato:
 *
 * {
 *     data: [
 *      {
 *          .....
 *      },
 *      {
 *          .....
 *      }
 *     ]
 * }
 *
 * @param <T>
 */
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseListWrapper<T> {

    /**
     * Data
     */
    private T data;

}