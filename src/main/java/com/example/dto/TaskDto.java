package com.example.dto;

import com.example.entity.elements.Priority;
import com.example.entity.elements.Status;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TaskDto (
    UUID id,
    @NotNull(message = "Title cannot be null")
    String title,
    @NotNull(message = "Description cannot be null")
    String description,
    @NotNull(message = "Status cannot be null")
    Status status,
    @NotNull(message = "Priority cannot be null")
    Priority priority
) {

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public Priority priority() {
        return priority;
    }

}
