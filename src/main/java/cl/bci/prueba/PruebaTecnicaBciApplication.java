package cl.bci.prueba;

import cl.bci.prueba.entity.PhoneEntity;
import cl.bci.prueba.entity.UserEntity;
import cl.bci.prueba.repository.PhoneRepository;
import cl.bci.prueba.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class PruebaTecnicaBciApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(PruebaTecnicaBciApplication.class, args);
	}

	//Esto es para generar data de prueba en bd

	@Autowired private UserRepository userRepository;

	@Autowired private PhoneRepository phoneRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		UserEntity userEntity1 = UserEntity.builder()
				.name("User uno")
				.email("user@uno.cl")
				.password("P4s5WorD")
				.build();

		UserEntity userEntity2 = UserEntity.builder()
				.name("User dos")
				.email("user@dos.cl")
				.password("PassW0Rd")
				.build();

		UserEntity userEntity3 = UserEntity.builder()
				.name("User tres")
				.email("user@tres.cl")
				.password("P455W0Rd")
				.build();

		userEntity1 = userRepository.save(userEntity1);
		userEntity2 = userRepository.save(userEntity2);
		userEntity3 = userRepository.save(userEntity3);


		PhoneEntity phoneEntity1 = PhoneEntity.builder()
				.userEntity(userEntity1)
				.number("123456789")
				.cityCode("1")
				.countryCode("100")
				.build();

		PhoneEntity phoneEntity2 = PhoneEntity.builder()
				.userEntity(userEntity1)
				.number("987654321")
				.cityCode("3")
				.countryCode("144")
				.build();

		PhoneEntity phoneEntity3 = PhoneEntity.builder()
				.userEntity(userEntity2)
				.number("12345986")
				.cityCode("2")
				.countryCode("202")
				.build();

		PhoneEntity phoneEntity4 = PhoneEntity.builder()
				.userEntity(userEntity3)
				.number("987651234")
				.cityCode("5")
				.countryCode("303")
				.build();

		phoneRepository.saveAll(Arrays.asList(phoneEntity1, phoneEntity2, phoneEntity3, phoneEntity4));

	}
}
