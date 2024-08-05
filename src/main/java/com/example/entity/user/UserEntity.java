package com.example.entity.user;

import com.example.dto.TaskDto;
import com.example.entity.task.RpTask;
import com.example.entity.task.Task;
import com.example.exceptions.NoDataException;
import com.jcabi.jdbc.JdbcSession;

import javax.sql.DataSource;
import java.awt.desktop.OpenFilesEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record UserEntity(
        UUID id,
        String email,
        String password,
        DataSource dataSource
) implements User {

    @Override
    public List<Task> createdTasks() throws SQLException {
        RpTask rpTask = new RpTask(dataSource);
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession.sql("""
                SELECT task_id FROM user_author_tasks
                WHERE user_id = ?
                """)
                .set(id)
                .select((resultSet, statement) -> {
                    List<UUID> taskIds = new ArrayList<>();
                    while (resultSet.next()) {
                        taskIds.add(UUID.fromString(resultSet.getString("task_id")));
                    }
                    return taskIds;
                }).stream().map(taskId -> {
                    try {
                        Optional<Task> taskOptional = rpTask.get(taskId);
                        return taskOptional.orElse(null);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    @Override
    public List<Task> assignedTasks() throws SQLException {
        RpTask rpTask = new RpTask(dataSource);
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession.sql("""
                SELECT task_id FROM user_executor_tasks
                WHERE user_id = ?
                """)
                .set(id)
                .select((resultSet, statement) -> {
                    List<UUID> taskIds = new ArrayList<>();
                    while (resultSet.next()) {
                        taskIds.add(UUID.fromString(resultSet.getString("task_id")));
                    }
                    return taskIds;
                }).stream().map(taskId -> {
                    try {
                        Optional<Task> taskOptional = rpTask.get(taskId);
                        return taskOptional.orElse(null);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    @Override
    public void createTask(TaskDto task) throws SQLException {
        RpTask rpTask = new RpTask(dataSource);
        try {
            rpTask.add(task, id);
        } catch (NoDataException e) {
            throw new RuntimeException(e);
        }
    }
}
