package com.skripok.bloodpressure.common.web.exception;

import com.skripok.bloodpressure.common.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.skripok.bloodpressure")
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request) {

        log.debug("Resource not found: {}", exception.getMessage());

        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );

        problem.setTitle("Resource not found");
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(
            IllegalArgumentException exception,
            HttpServletRequest request) {

        log.warn("Invalid request [{}]: {}", request.getRequestURI(), exception.getMessage());

        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );

        problem.setTitle("Invalid request");
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(
            Exception exception,
            HttpServletRequest request) {

        log.error("Unexpected error [{}]", request.getRequestURI(), exception);

        final ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected server error occurred"
        );

        problem.setTitle("Internal server error");
        return problem;
    }
}