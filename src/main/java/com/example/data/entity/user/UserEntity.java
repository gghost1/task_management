package com.example.data.entity.user;

import com.example.data.entity.task.RpTask;
import com.example.data.entity.task.Task;
import com.example.data.request.PaginationEntityRequest;
import com.jcabi.jdbc.JdbcSession;

import javax.sql.DataSource;
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
    public List<Task> createdTasks(PaginationEntityRequest paginationEntityRequest) throws SQLException {
        RpTask rpTask = new RpTask(dataSource);
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession.sql("""
                SELECT task_id FROM user_author_tasks
                WHERE user_id = ?
                LIMIT ? OFFSET ?
                """)
                .set(id)
                .set(paginationEntityRequest.size())
                .set(paginationEntityRequest.offset())
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
    public List<Task> assignedTasks(PaginationEntityRequest paginationEntityRequest) throws SQLException {
        RpTask rpTask = new RpTask(dataSource);
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession.sql("""
                SELECT task_id FROM user_executor_tasks
                WHERE user_id = ?
                LIMIT ? OFFSET ?
                """)
                .set(id)
                .set(paginationEntityRequest.size())
                .set(paginationEntityRequest.offset())
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
}
