package com.example.data.entity.user;

import com.example.data.request.PaginationEntityRequest;
import com.example.data.entity.task.Task;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface User {
    UUID id();
    String email();
    String password();
    List<Task> createdTasks(PaginationEntityRequest paginationEntityRequest) throws SQLException;
    List<Task> assignedTasks(PaginationEntityRequest paginationEntityRequest) throws SQLException;
}
