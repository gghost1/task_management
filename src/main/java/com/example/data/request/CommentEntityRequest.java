package com.example.data.request;

import jakarta.validation.constraints.NotNull;

public record CommentEntityRequest(
        @NotNull(message = "Text cannot be null")
        String text
) {
    @Override
    public String text() {
        return text;
    }
}
