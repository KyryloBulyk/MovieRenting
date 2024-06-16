package com.ibm.quarkus.academy.tmdb.connector.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.quarkus.academy.tmdb.connector.model.Movie;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class MovieSerializer implements Serializer<Movie> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Nothing to configure
    }

    @Override
    public byte[] serialize(String topic, Movie data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing movie to JSON", e);
        }
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
