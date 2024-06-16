package com.ibm.quarkus.academy.common.event;

import com.ibm.quarkus.academy.ram.model.Movie;
import com.ibm.quarkus.academy.ram.repository.MovieRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MovieConsumerService {

    @Inject
    MovieRepository movieRepository;

    private static final Logger LOGGER = Logger.getLogger(MovieConsumerService.class);

    @Incoming("discoveries-in")
    @Transactional
    public void consume(Movie movie) {
        try {
            LOGGER.infof("Received movie from Kafka: %s", movie.getTitle());
            Movie movieInDB = movieRepository.findById(movie.getId());

            if(movieInDB != null) {
                LOGGER.infof("Existing movie in local DB: %s", movie);
                return;
            }

            movieRepository.persist(movie);
            LOGGER.infof("Persisted movie: %s", movie);
        } catch (Exception e) {
            LOGGER.error("Error persisting movie", e);
        }
    }
}
