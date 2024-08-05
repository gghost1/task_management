package com.example.response;

import lombok.Data;

@Data
public class BasicResponce<T> {
    public String jwt;
    public T data;

    public BasicResponce(String jwt, T data) {
        this.jwt = jwt;
        this.data = data;
    }

}
