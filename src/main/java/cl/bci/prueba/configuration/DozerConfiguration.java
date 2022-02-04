package cl.bci.prueba.configuration;

import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Clase de configuración para DozerBeanMapper
 */
@Configuration
public class DozerConfiguration {

    /**
     * Bean de DozerBeanMapper con soporte para tipos de JDK8
     * @return
     */
    @Bean
    public DozerBeanMapper dozerBeanMapper() {
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        dozerBeanMapper.setMappingFiles(Collections.singletonList("dozerJDK8.xml"));
        return dozerBeanMapper;
    }

}
