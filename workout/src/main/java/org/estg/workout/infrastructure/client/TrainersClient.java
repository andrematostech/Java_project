package org.estg.workout.infrastructure.client;

import org.estg.workout.config.ExternalServicesProperties;
import org.estg.workout.dto.TrainerSummary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * REST client used by Workout service
 * to communicate with Trainers service.
 */
@Component // Spring-managed bean
public class TrainersClient {

    private final RestTemplate restTemplate;        // HTTP client
    private final ExternalServicesProperties props; // config holder

    // Constructor injection
    public TrainersClient(RestTemplate restTemplate,
                          ExternalServicesProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    /**
     * Fetch trainer by id from Trainers service.
     * Returns null if trainer does not exist.
     */
    public TrainerSummary getById(String trainerId) {

        // Base URL from application.yml
        String baseUrl = props.getServices()
                .getTrainers()
                .getBaseUrl();

        // Full endpoint URL
        String url = baseUrl + ExternalApiPaths.TRAINERS_GET_BY_ID;

        try {
            // Call Trainers API
            return restTemplate.getForObject(
                    url,
                    TrainerSummary.class,
                    trainerId
            );
        } catch (HttpClientErrorException.NotFound e) {
            // Trainer not found
            return null;
        }
    }
}
