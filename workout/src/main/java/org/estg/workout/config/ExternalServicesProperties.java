package org.estg.workout.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Reads gymhub.* settings from application.yml.
 * Keeps URLs and feature flags in one place.
 */
@Component // Spring creates this bean automatically
@ConfigurationProperties(prefix = "gymhub") // binds gymhub.* from YAML
public class ExternalServicesProperties {

    // gymhub.validation.enabled
    private Validation validation = new Validation();

    // gymhub.services.members / trainers
    private Services services = new Services();

    public Validation getValidation() { // getter
        return validation;
    }

    public void setValidation(Validation validation) { // setter
        this.validation = validation;
    }

    public Services getServices() { // getter
        return services;
    }

    public void setServices(Services services) { // setter
        this.services = services;
    }

    // Nested object for "gymhub.validation"
    public static class Validation {

        // enables/disables calling other services
        private boolean enabled = true;

        public boolean isEnabled() { // getter
            return enabled;
        }

        public void setEnabled(boolean enabled) { // setter
            this.enabled = enabled;
        }
    }

    // Nested object for "gymhub.services"
    public static class Services {

        // config for members service
        private ServiceEndpoint members = new ServiceEndpoint();

        // config for trainers service
        private ServiceEndpoint trainers = new ServiceEndpoint();

        public ServiceEndpoint getMembers() { // getter
            return members;
        }

        public void setMembers(ServiceEndpoint members) { // setter
            this.members = members;
        }

        public ServiceEndpoint getTrainers() { // getter
            return trainers;
        }

        public void setTrainers(ServiceEndpoint trainers) { // setter
            this.trainers = trainers;
        }
    }

    // Generic endpoint object
    public static class ServiceEndpoint {

        // base url like http://localhost:8081
        private String baseUrl;

        public String getBaseUrl() { // getter
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) { // setter
            this.baseUrl = baseUrl;
        }
    }
}
