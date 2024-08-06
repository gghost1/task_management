package com.example.data.response;

import lombok.Data;

@Data
public class BasicResponse<T> {

    public String jwt;
    public T data;

    public BasicResponse(String jwt, T data) {
        this.jwt = jwt;
        this.data = data;
    }
}
