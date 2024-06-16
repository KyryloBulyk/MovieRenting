package com.ibm.quarkus.academy.ram.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.quarkus.academy.ram.model.Movie;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class MovieDeserializer implements Deserializer<Movie> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Nothing to configure
    }

    @Override
    public Movie deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, Movie.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing movie from JSON", e);
        }
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
