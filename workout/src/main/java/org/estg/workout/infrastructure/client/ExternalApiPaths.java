package org.estg.workout.infrastructure.client;

/**
 * Centralizes external REST endpoint paths.
 * If Members/Trainers endpoints change later,
 * you update ONLY this file.
 */
public final class ExternalApiPaths {

    // prevent instantiation
    private ExternalApiPaths() {}

    // Members service: get member by id
    public static final String MEMBERS_GET_BY_ID = "/api/members/{id}";

    // Trainers service: get trainer by id
    public static final String TRAINERS_GET_BY_ID = "/api/trainers/{id}";
}
