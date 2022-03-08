package personalwebsite.personalweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PersonalwebApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalwebApplication.class, args);
	}

}
