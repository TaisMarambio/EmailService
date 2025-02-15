package com.challenge.emailservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
class EmailServiceApplicationTests {

	@Test
	void contextLoads() {
		// Solo verifica que el contexto arranca correctamente
	}
}
