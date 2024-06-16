package com.ibm.quarkus.academy.tmdb.connector.exceptioon;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(String message) {
        super(message);
    }
}
