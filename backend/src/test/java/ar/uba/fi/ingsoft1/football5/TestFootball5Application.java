package ar.uba.fi.ingsoft1.football5;

import org.springframework.boot.SpringApplication;

public class TestFootball5Application {

	public static void main(String[] args) {
		SpringApplication.from(Football5Application::main).with(TestContainersConfiguration.class).run(args);
	}

}
