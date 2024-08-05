package com.example.response;

import lombok.Data;

@Data
public class ErrorResponse <T>{
    public String jwt;
    public T error;

    public ErrorResponse(String jwt, T error) {
        this.jwt = jwt;
        this.error = error;
    }
}
