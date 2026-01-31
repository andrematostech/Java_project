package org.estg.trainers;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TrainersApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(TrainersApplicationTests.class);

    @Test
    void contextLoads() {
        // Context load sanity check
        log.info("? ? TESTE PASSOU COM SUCESSO ? ? ?");
    }
}
