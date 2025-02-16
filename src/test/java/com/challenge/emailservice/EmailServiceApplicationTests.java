package com.challenge.emailservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@TestPropertySource(locations = "classpath:application-test.properties")
class EmailServiceApplicationTests {

	@Test
	void contextLoads() {
		// Solo verifica que el contexto arranca correctamente
	}
}
