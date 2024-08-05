package com.example.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CommentDto (
        UUID id,
        @NotNull(message = "Text cannot be null")
        String text
) {
    @Override
    public UUID id() {
        return id;
    }

    @Override
    public String text() {
        return text;
    }

}
