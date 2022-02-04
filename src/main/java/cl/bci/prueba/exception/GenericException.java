package cl.bci.prueba.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Excepción Generica
 */
@Getter
public class GenericException extends RuntimeException {

    /**
     * Http status
     */
    private final HttpStatus httpStatus;

    /**
     * Detalles
     */
    private final List<String> details = new ArrayList<>();

    /**
     * Constructor
     * @param httpStatus Httpstatus
     */
    public GenericException(HttpStatus httpStatus) {
        super(httpStatus.getReasonPhrase());
        this.httpStatus = httpStatus;
    }

    /**
     * Método que permite setear una lista de detalles
     * @param details
     * @return
     */
    public GenericException details(List<String> details) {
        this.details.addAll(details);
        return this;
    }

    /**
     * Metodo que permite agregar un detalle
     * @param detail
     * @return
     */
    public GenericException detail(String detail) {
        this.details.add(detail);
        return this;
    }

}
