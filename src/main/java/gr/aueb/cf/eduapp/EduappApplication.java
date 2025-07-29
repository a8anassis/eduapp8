package gr.aueb.cf.eduapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EduappApplication {

	public static void main(String[] args) {
		SpringApplication.run(EduappApplication.class, args);
	}

}
