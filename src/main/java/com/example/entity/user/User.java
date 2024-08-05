package com.example.entity.user;

import com.example.dto.TaskDto;
import com.example.entity.task.Task;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface User {
    UUID id();
    String email();
    String password();
    List<Task> createdTasks() throws SQLException;
    List<Task> assignedTasks() throws SQLException;
    void createTask(TaskDto task) throws SQLException;

}
