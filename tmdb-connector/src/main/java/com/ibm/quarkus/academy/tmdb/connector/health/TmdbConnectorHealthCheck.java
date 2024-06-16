package com.ibm.quarkus.academy.tmdb.connector.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.openapi.quarkus.tmdb_yaml.api.DefaultApi;

@ApplicationScoped
@Readiness
public class TmdbConnectorHealthCheck implements HealthCheck {

    private static final Logger LOGGER = Logger.getLogger(TmdbConnectorHealthCheck.class);

    @Inject
    @RestClient
    DefaultApi defaultApi;

    @Override
    public HealthCheckResponse call() {
        try {
            LOGGER.info("Performing readiness check");
            defaultApi.movieDetails(2, null, null);
            return HealthCheckResponse.up("TmdbConnector is ready");
        } catch (Exception e) {
            LOGGER.error("Readiness check failed", e);
            return HealthCheckResponse.down("TmdbConnector is not ready");
        }
    }
}