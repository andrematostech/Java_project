package org.estg.schedule.infrastructure.client;

import org.estg.schedule.exceptions.SessionConflictException;
import org.estg.schedule.infrastructure.client.dto.MemberProfileResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class MembersClient {

    private final RestClient restClient;

    public MembersClient(RestClient.Builder loadBalancedRestClientBuilder) {
        this.restClient = loadBalancedRestClientBuilder
                .baseUrl("http://members-service")
                .build();
    }

    public void requireActiveMember(String memberId) {
        MemberProfileResponse profile = getMemberProfile(memberId);

        if (profile == null || profile.getMember() == null) {
            throw new SessionConflictException("Member profile not found");
        }

        String status = profile.getMember().getStatus();
        if (status == null || !"ACTIVE".equalsIgnoreCase(status)) {
            throw new SessionConflictException("Member is not active");
        }
    }

    private MemberProfileResponse getMemberProfile(String memberId) {
        try {
            return restClient.get()
                    .uri("/api/members/{id}/profile", memberId)
                    .retrieve()
                    .body(MemberProfileResponse.class);
        } catch (RestClientResponseException ex) {
            HttpStatusCode status = ex.getStatusCode();
            if (status != null && status.value() == 404) {
                throw new SessionConflictException("Member not found");
            }
            throw new SessionConflictException("Members service call failed: " + ex.getMessage());
        } catch (RestClientException ex) {
            throw new SessionConflictException("Members service unavailable: " + ex.getMessage());
        }
    }
}
