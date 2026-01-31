package org.estg.schedule;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ScheduleApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(ScheduleApplicationTests.class);

	@Test
	void contextLoads() {
	}

	@AfterEach
	void banner() {
		log.info("? ? TESTE PASSOU COM SUCESSO ? ? ?");
	}

}
