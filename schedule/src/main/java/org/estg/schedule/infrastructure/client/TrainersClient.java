package org.estg.schedule.infrastructure.client;

import org.estg.schedule.exceptions.SessionConflictException;
import org.estg.schedule.infrastructure.client.dto.TrainerAvailabilityResponse;
import org.estg.schedule.infrastructure.client.dto.TrainerProfileResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;

@Component
public class TrainersClient {

    private final RestClient restClient;

    public TrainersClient(RestClient.Builder loadBalancedRestClientBuilder) {
        this.restClient = loadBalancedRestClientBuilder
                .baseUrl("http://trainers-service")
                .build();
    }

    public void requireActiveTrainer(String trainerId) {
        TrainerProfileResponse trainer = getTrainerProfile(trainerId);

        if (trainer == null) {
            throw new SessionConflictException("Trainer not found");
        }

        String status = trainer.getStatus();
        if (status == null || !"ACTIVE".equalsIgnoreCase(status)) {
            throw new SessionConflictException("Trainer is not active");
        }
    }

    public void requireTrainerAvailable(String trainerId, LocalDateTime startTime, LocalDateTime endTime) {
        TrainerAvailabilityResponse response = getTrainerAvailability(trainerId, startTime, endTime);

        if (response == null) {
            throw new SessionConflictException("Trainer availability could not be verified");
        }
        if (!response.isAvailable()) {
            throw new SessionConflictException("Trainer is not available for the selected time slot");
        }
    }

    private TrainerProfileResponse getTrainerProfile(String trainerId) {
        try {
            return restClient.get()
                    .uri("/api/trainers/{id}", trainerId)
                    .retrieve()
                    .body(TrainerProfileResponse.class);
        } catch (RestClientResponseException ex) {
            HttpStatusCode status = ex.getStatusCode();
            if (status != null && status.value() == 404) {
                throw new SessionConflictException("Trainer not found");
            }
            throw new SessionConflictException("Trainers service call failed: " + ex.getMessage());
        } catch (RestClientException ex) {
            throw new SessionConflictException("Trainers service unavailable: " + ex.getMessage());
        }
    }

    private TrainerAvailabilityResponse getTrainerAvailability(String trainerId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/trainers/{id}/available")
                            .queryParam("startTime", startTime)
                            .queryParam("endTime", endTime)
                            .build(trainerId))
                    .retrieve()
                    .body(TrainerAvailabilityResponse.class);
        } catch (RestClientResponseException ex) {
            HttpStatusCode status = ex.getStatusCode();
            if (status != null && status.value() == 404) {
                throw new SessionConflictException("Trainer not found");
            }
            throw new SessionConflictException("Trainer availability call failed: " + ex.getMessage());
        } catch (RestClientException ex) {
            throw new SessionConflictException("Trainers service unavailable: " + ex.getMessage());
        }
    }
}
