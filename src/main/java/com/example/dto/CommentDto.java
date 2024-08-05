package com.example.dto;

import java.util.UUID;

public record CommentDto (
        UUID id,
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
