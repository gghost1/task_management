package com.example.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record PaginationDto (
    @NotNull(message = "Page cannot be null")
    @Min(value = 0, message = "Page cannot be less than 0")
    int page,
    @NotNull(message = "Size cannot be null")
    @Min(value = 5, message = "Size cannot be less than 5")
    @Max(value = 20, message = "Size cannot be greater than 20")
    int size
) {
    @Override
    public int page() {
        return page;
    }

    @Override
    public int size() {
        return size;
    }

    public int offset() {
        return page * size;
    }
}
