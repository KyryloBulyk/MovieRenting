package com.ibm.quarkus.academy.ram.handler;

import com.ibm.quarkus.academy.ram.exception.MovieAlreadyRentedException;
import com.ibm.quarkus.academy.ram.exception.MovieNotFoundException;
import com.ibm.quarkus.academy.ram.exception.UserNotFoundException;

import com.ibm.quarkus.academy.ram.exception.UserOperationException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Throwable exception) {
        LOGGER.error("An unexpected error occurred: ", exception);
        ErrorResponse errorResponse = new ErrorResponse();

        if (exception instanceof UserNotFoundException) {
            errorResponse.setTitle("User Not Found");
            errorResponse.setDetail(exception.getMessage());
            errorResponse.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        } else if (exception instanceof MovieNotFoundException) {
            errorResponse.setTitle("Movie Not Found");
            errorResponse.setDetail(exception.getMessage());
            errorResponse.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        } else if (exception instanceof MovieAlreadyRentedException) {
            errorResponse.setTitle("Movie Already Rented");
            errorResponse.setDetail(exception.getMessage());
            errorResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        } else if (exception instanceof UserOperationException) {
            errorResponse.setTitle("User Operation Error");
            errorResponse.setDetail(exception.getMessage());
            errorResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        } else if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) exception;
            String errors = constraintViolationException.getConstraintViolations()
                    .stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            errorResponse.setTitle("Constraint Violation");
            errorResponse.setDetail(errors);
            errorResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        } else if (exception instanceof NotFoundException) {
            errorResponse.setTitle("Resource Not Found");
            errorResponse.setDetail(exception.getMessage());
            errorResponse.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        } else if (exception instanceof ClientErrorException) {
            ClientErrorException clientErrorException = (ClientErrorException) exception;
            if (clientErrorException.getResponse().getStatus() == Response.Status.METHOD_NOT_ALLOWED.getStatusCode()) {
                errorResponse.setTitle("Method Not Allowed");
                errorResponse.setDetail("The requested method is not allowed for the specified resource.");
                errorResponse.setStatus(Response.Status.METHOD_NOT_ALLOWED.getStatusCode());
                return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity(errorResponse).build();
            } else if (clientErrorException.getResponse().getStatus() == Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE.getStatusCode()) {
                errorResponse.setTitle("Requested Range Not Satisfiable");
                errorResponse.setDetail("The requested range is not satisfiable.");
                errorResponse.setStatus(Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE.getStatusCode());
                return Response.status(Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE).entity(errorResponse).build();
            }
        }

        errorResponse.setTitle("Internal Server Error");
        errorResponse.setDetail("An unexpected error occurred");
        errorResponse.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
    }
}
