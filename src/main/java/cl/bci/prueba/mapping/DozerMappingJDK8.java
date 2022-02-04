package cl.bci.prueba.mapping;

import org.dozer.CustomConverter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Clase que soporta mapper para tipos de Java8
 */
public class DozerMappingJDK8 implements CustomConverter {

    /**
     * Convert
     * @param destination
     * @param source
     * @param destinationClass
     * @param sourceClass
     * @return
     */
    @Override
    public Object convert(Object destination, Object source, Class<?> destinationClass, Class<?> sourceClass) {

        if (destinationClass == null || sourceClass == null) {
            return destination;
        }

        if (source == null) {
            destination = null;
        } else if (destinationClass.isAssignableFrom(LocalDateTime.class) && sourceClass.isAssignableFrom(LocalDateTime.class)) {
            destination = localDateTimeCreator(source);
        } else if (destinationClass.isAssignableFrom(UUID.class) && sourceClass.isAssignableFrom(UUID.class)) {
            destination = uuidCreator(source);
        }

        return destination;
    }

    /**
     * Método para copiar valores de un objeto LocalDateTime a otro
     * @param source
     * @return
     */
    private LocalDateTime localDateTimeCreator(Object source) {
        LocalDateTime srcObject = (LocalDateTime) source;
        return LocalDateTime.of(
                srcObject.getYear(),
                srcObject.getMonth(),
                srcObject.getDayOfMonth(),
                srcObject.getHour(),
                srcObject.getMinute(),
                srcObject.getSecond(),
                srcObject.getNano()
        );
    }

    /**
     * Método que copia el valor de un UUID a otro objeto
     * @param source
     * @return
     */
    private UUID uuidCreator(Object source) {
        UUID srcObject = (UUID) source;
        return UUID.fromString(srcObject.toString());
    }

}
