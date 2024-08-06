package com.example.data.request;

import com.example.data.entity.elements.Priority;
import com.example.data.entity.elements.Status;
import jakarta.validation.constraints.NotNull;

public record TaskEntityRequest(
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
