package ar.uba.fi.ingsoft1.football5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Football5Application {

	public static void main(String[] args) {
		SpringApplication.run(Football5Application.class, args);
	}

}
