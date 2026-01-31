package org.estg.notifications;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Use H2 + disable Eureka during tests
class NotificationsApplicationTests {

    @Test
    void contextLoads() {
        // Validates Spring context starts
    }
}
