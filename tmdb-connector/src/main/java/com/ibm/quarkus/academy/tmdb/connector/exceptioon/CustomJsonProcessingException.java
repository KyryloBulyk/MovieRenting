package com.ibm.quarkus.academy.tmdb.connector.exceptioon;

public class CustomJsonProcessingException extends RuntimeException {
    public CustomJsonProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
