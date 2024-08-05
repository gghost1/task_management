package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserDto(
        UUID id,
        @NotNull(message = "Email cannot be null")
        @Email(message = "Email should be valid")
        String email,
        @NotNull(message = "Password cannot be null")
        @Size(min = 6, message = "Password should be at least 6 characters long")
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
