package cl.bci.prueba.configuration;

import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class DozerConfiguration {

    @Bean
    public DozerBeanMapper dozerBeanMapper() {
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        dozerBeanMapper.setMappingFiles(Collections.singletonList("dozerJDK8.xml"));
        return dozerBeanMapper;
    }

}
