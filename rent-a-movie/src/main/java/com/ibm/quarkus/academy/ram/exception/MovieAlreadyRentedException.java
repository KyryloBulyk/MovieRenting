package com.ibm.quarkus.academy.ram.exception;

public class MovieAlreadyRentedException extends RuntimeException {
    public MovieAlreadyRentedException(String message) {
        super(message);
    }
}
