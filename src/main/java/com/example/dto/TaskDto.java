package com.example.dto;

import com.example.entity.elements.Priority;
import com.example.entity.elements.Status;

import java.util.UUID;

public record TaskDto (
    UUID id,
    String title,
    String description,
    Status status,
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
