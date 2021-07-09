package cl.bci.prueba.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GenericException extends RuntimeException {

    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private List<String> details = new ArrayList<>();

    private GenericException(String message) {
        super(message);
    }

    public static GenericException newInstance(String message) {
        return new GenericException(message);
    }

    public GenericException httpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public GenericException details(List<String> details) {
        this.details = details;
        return this;
    }

    public GenericException detail(String detail) {
        this.details.add(detail);
        return this;
    }

}
