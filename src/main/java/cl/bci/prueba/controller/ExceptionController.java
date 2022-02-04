package cl.bci.prueba.controller;

import cl.bci.prueba.dto.GenericResponseDto;
import cl.bci.prueba.exception.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controlador de excepciones
 * Captura todas las excepciones no controladas.
 */
@RestControllerAdvice
@Slf4j
public class ExceptionController extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logError(exception);
        List<String> details = new ArrayList<>();
        exception.getBindingResult().getAllErrors().forEach(ex -> details.add(ex.getDefaultMessage()));
        GenericResponseDto error = GenericResponseDto.builder().message(HttpStatus.BAD_REQUEST.getReasonPhrase()).details(details).build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Manejador de excepciones de tipo ErrorException que son excepciones propias personalizadas
     *
     * @param exception Excepcion capturada
     * @return ErrorResponse con detalles del error ocurrido
     */
    @ExceptionHandler(GenericException.class)
    public ResponseEntity<Object> handleErrorException(GenericException exception) {
        logError(exception);
        GenericResponseDto error = GenericResponseDto.builder().message(exception.getMessage()).details(exception.getDetails()).build();
        return new ResponseEntity<>(error, exception.getHttpStatus());
    }

    /**
     * Manejador de excepciones de tipo AccessDeniedException que son lanzadas cuando un usuario no tiene acceso a un recurso
     *
     * @param exception Excepcion capturada
     * @return ErrorResponse con detalles del error ocurrido
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception) {
        logError(exception);
        GenericResponseDto error = GenericResponseDto.builder().message(exception.getMessage()).details(Collections.singletonList(HttpStatus.UNAUTHORIZED.getReasonPhrase())).build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Manejador de excepciones de cualquier tipo, es decir cualquier excepcion no controlada
     *
     * @param exception Excepcion capturada
     * @return ErrorResponse con detalles del error ocurrido
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception exception) {
        logError(exception);
        GenericResponseDto error = GenericResponseDto.builder().message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logError(Exception exception) {
        log.error("[ExceptionController] Excepcion capturada", exception);
    }

}
