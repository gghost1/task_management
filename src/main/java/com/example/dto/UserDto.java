package com.example.dto;


import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        String password
) {

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String password() {
        return password;
    }
}
