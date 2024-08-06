package com.example.configuration.exception;

import com.example.data.response.ErrorResponse;
import com.example.configuration.security.AuthTokenFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionsHandler {

    AuthTokenFilter authTokenFilter = new AuthTokenFilter();

    @ExceptionHandler({NoDataException.class, NotAvailableException.class})
    public ResponseEntity<?> handleNoDataException(NoDataException ex, HttpServletRequest request) {
        String jwt = authTokenFilter.parseJwt(request);
        return ResponseEntity.internalServerError().body(new ErrorResponse<>(jwt, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        String jwt = authTokenFilter.parseJwt(request);
        return ResponseEntity.badRequest().body(new ErrorResponse<>(jwt, ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String jwt = authTokenFilter.parseJwt(request);
        return ResponseEntity.badRequest().body(new ErrorResponse<>(jwt, "Argument not valid"));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> handleSQLException(SQLException ex, HttpServletRequest request) {
        String jwt = authTokenFilter.parseJwt(request);
        return ResponseEntity.internalServerError().body(new ErrorResponse<>(jwt, ex.getMessage()));
    }

    @ExceptionHandler(NotValidParam.class)
    public ResponseEntity<?> handleNotValidParam(NotValidParam ex, HttpServletRequest request) {
        String jwt = authTokenFilter.parseJwt(request);
        return ResponseEntity.badRequest().body(new ErrorResponse<>(jwt, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String jwt = authTokenFilter.parseJwt(request);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(new ErrorResponse<>(jwt, errors));
    }
}
