package com.example.resiliencemap.functional;


import com.example.resiliencemap.functional.exception.*;
import com.example.resiliencemap.functional.model.ValidationFieldErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException e) {
        return new ResponseEntity<>(createResponseBody(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(Exception e) {
        return new ResponseEntity<>(createResponseBody("Forbidden"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthentication(Exception e) {
        logger.info("[handleAuthentication] : ", e);
        return new ResponseEntity<>(createResponseBody("Unauthorized"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException e) {
        logger.info(String.format("[handleNoResourceFoundException] : method type: %s, method path: %s", e.getHttpMethod().name(), e.getResourcePath()));
        return new ResponseEntity<>(createResponseBody(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        logger.info(String.format("[handleAuthorizationDeniedException] message: %s; isGranted: %s", e.getMessage(), e.getAuthorizationResult().isGranted()));
        return new ResponseEntity<>(createResponseBody(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflictException(ConflictException e) {
        return new ResponseEntity<>(createResponseBody(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(createResponseBody(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException e) {
        return new ResponseEntity<>(createResponseBody(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleForbiddenException(BadRequestException e) {
        return new ResponseEntity<>(createResponseBody(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ValidationFieldErrorResponse> fields = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> new ValidationFieldErrorResponse(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        Map<String, Object> response = new HashMap<>();
        response.put("errors", fields);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        logger.error("[handleException] : ", e);
        createResponseBody("INTERNAL_SERVER_ERROR");
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> createResponseBody(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        return body;
    }
}
