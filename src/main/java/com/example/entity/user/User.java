package com.example.entity.user;

import com.example.dto.PaginationDto;
import com.example.dto.TaskDto;
import com.example.entity.task.Task;
import jakarta.validation.Valid;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface User {
    UUID id();
    String email();
    String password();
    List<Task> createdTasks(PaginationDto paginationDto) throws SQLException;
    List<Task> assignedTasks(@Valid PaginationDto paginationDto) throws SQLException;
}
