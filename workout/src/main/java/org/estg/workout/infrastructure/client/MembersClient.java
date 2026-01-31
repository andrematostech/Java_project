package org.estg.workout.infrastructure.client;

import org.estg.workout.config.ExternalServicesProperties;
import org.estg.workout.dto.MemberSummary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * REST client used by Workout service
 * to communicate with Members service.
 */
@Component // Spring-managed bean
public class MembersClient {

    private final RestTemplate restTemplate;           // HTTP client
    private final ExternalServicesProperties props;    // config holder

    // Constructor injection
    public MembersClient(RestTemplate restTemplate,
                         ExternalServicesProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    /**
     * Fetch member by id from Members service.
     * Returns null if member does not exist.
     */
    public MemberSummary getById(String memberId) {

        // Base URL from application.yml
        String baseUrl = props.getServices()
                .getMembers()
                .getBaseUrl();

        // Full endpoint URL
        String url = baseUrl + ExternalApiPaths.MEMBERS_GET_BY_ID;

        try {
            // Call Members API
            return restTemplate.getForObject(
                    url,
                    MemberSummary.class,
                    memberId
            );
        } catch (HttpClientErrorException.NotFound e) {
            // Member not found
            return null;
        }
    }
}
