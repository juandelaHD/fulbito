package ar.uba.fi.ingsoft1.football5;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestContainersConfiguration.class)
@SpringBootTest
class Football5ApplicationTests {

	@Test
	void contextLoads() {
	}

}
