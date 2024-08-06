package com.example.data.entity.task;

import com.example.data.entity.comment.RpComment;
import com.example.data.entity.user.User;
import com.example.data.request.PaginationEntityRequest;
import com.example.data.request.TaskEntityRequest;
import com.example.data.entity.elements.Priority;
import com.example.data.entity.elements.Status;
import com.example.data.entity.user.RpUser;
import com.example.configuration.exception.NoDataException;
import com.example.configuration.exception.NotAvailableException;
import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Outcome;
import com.jcabi.jdbc.SingleOutcome;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

@Component
public class RpTask {

    private final DataSource dataSource;
    private final RpUser rpUser;
    private final RpComment rpComment;

    public RpTask(DataSource dataSource) {
        this.dataSource = dataSource;
        this.rpComment = new RpComment(dataSource);
        this.rpUser = new RpUser(dataSource);
    }

    private boolean checkPermission(UUID task_id, UUID user_id, JdbcSession jdbcSession) throws SQLException, NoDataException {
        Optional<User> user = rpUser.get(user_id);
        if (user.isEmpty()) {
            throw new NoDataException("User not found");
        }
        Optional<UUID> taskAuthor = jdbcSession.sql("""
                SELECT user_id FROM user_author_tasks
                WHERE task_id = ?
                """)
                .set(task_id)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Optional.of(UUID.fromString(rset.getString("user_id")));
                    } else {
                        return Optional.empty();
                    }
                });

        if (taskAuthor.isEmpty()) {
            throw new NoDataException("Task not found");
        }
        return taskAuthor.get().equals(user_id);
    }

    public Task add(TaskEntityRequest task, UUID user_id) throws SQLException, NoDataException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        Optional<User> user = rpUser.get(user_id);
        if (user.isEmpty()) {
            throw new NoDataException("User not found");
        }

        UUID id = jdbcSession.sql("""
                        INSERT INTO tasks (title, description, status, priority)
                        VALUES (?, ?, CAST(? AS task_status), CAST(? AS task_priority))
                        """)
                .set(task.title())
                .set(task.description())
                .set(Status.NEW.toString())
                .set(task.priority().toString())
                .insert(new SingleOutcome<>(UUID.class));
        jdbcSession.sql("""
                INSERT INTO user_author_tasks (task_id, user_id)
                VALUES (?, ?)
                """)
                .set(id)
                .set(user_id)
                .insert(Outcome.VOID);
        return new TaskEntity(
                id,
                task.title(),
                task.description(),
                task.status(),
                task.priority(),
                dataSource
        );
    }

    public Optional<Task> get(UUID id) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT id, title, description, status, priority FROM tasks
                        WHERE id = ?
                        """)
                .set(id)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Optional.of(new TaskEntity(
                                UUID.fromString(rset.getString("id")),
                                rset.getString("title"),
                                rset.getString("description"),
                                Status.valueOf(rset.getString("status")),
                                Priority.valueOf(rset.getString("priority")),
                                dataSource
                        ));
                    } else {
                        return Optional.empty();
                    }
                });
    }

    public List<Task> getAll(PaginationEntityRequest paginationEntityRequest) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        return jdbcSession.sql("""
                SELECT id, title, description, status, priority FROM tasks
                LIMIT ? OFFSET ?
                """)
                .set(paginationEntityRequest.size())
                .set(paginationEntityRequest.offset())
                .select((rset, stmt) -> {
                    List<Task> tasks = new ArrayList<>();
                    while (rset.next()) {
                        tasks.add(new TaskEntity(
                                UUID.fromString(rset.getString("id")),
                                rset.getString("title"),
                                rset.getString("description"),
                                Status.valueOf(rset.getString("status")),
                                Priority.valueOf(rset.getString("priority")),
                                dataSource
                        ));
                    }
                    return tasks;
                });
    }

    public Task assign(UUID task_id, UUID userAssign_id, UUID user_id) throws SQLException, NoDataException, NotAvailableException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        if (!checkPermission(task_id, user_id, jdbcSession)) {
            throw new NotAvailableException("You don't have permission to assign this task");
        }


        Optional<User> userAssign = rpUser.get(userAssign_id);
        if (userAssign.isEmpty()) {
            throw new NoDataException("User to assign not found");
        }

        Optional<UUID> assigned_id = jdbcSession.sql("""
                SELECT user_id FROM user_executor_tasks
                WHERE task_id = ?
                """)
                .set(task_id)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Optional.of(UUID.fromString(rset.getString("user_id")));
                    } else {
                        return Optional.empty();
                    }
                });
        if (assigned_id.isEmpty()) {
            jdbcSession.sql("""
                INSERT INTO user_executor_tasks (task_id, user_id)
                VALUES (?, ?)
                """)
                    .set(task_id)
                    .set(userAssign_id)
                    .insert(Outcome.VOID);
        } else {
            jdbcSession.sql("""
                UPDATE user_executor_tasks
                SET user_id = ?
                WHERE task_id = ?
                """)
                    .set(userAssign_id)
                    .set(task_id)
                    .update(Outcome.VOID);
        }
        Optional<Task> task = get(task_id);
        if (task.isEmpty()) {
            throw new NoDataException("Task not found");
        }
        return task.get();
    }

    public void delete(UUID task_id, UUID user_id) throws SQLException, NoDataException, NotAvailableException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        if (!checkPermission(task_id, user_id, jdbcSession)) {
            throw new NotAvailableException("You don't have permission to delete this task");
        }

        jdbcSession.sql("""
                DELETE FROM user_executor_tasks
                WHERE task_id = ?
                """)
                .set(task_id)
                .update(Outcome.VOID);
        jdbcSession.sql("""
                DELETE FROM user_author_tasks
                WHERE task_id = ?
                """)
                .set(task_id)
                .update(Outcome.VOID);

        rpComment.deleteAllForTask(task_id);

        jdbcSession.sql("""
                DELETE FROM tasks
                WHERE id = ?
                """)
                .set(task_id)
                .update(Outcome.VOID);

    }


    public Task editTitle(UUID task_id, String title, UUID user_id) throws SQLException, NoDataException, NotAvailableException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        if (!checkPermission(task_id, user_id, jdbcSession)) {
            throw new NotAvailableException("You don't have permission to edit this task");
        }

        jdbcSession.sql("""
                UPDATE tasks
                SET title = ?
                WHERE id = ?
                """)
                .set(title)
                .set(task_id)
                .update(Outcome.VOID);
        Optional<Task> task = get(task_id);
        if (task.isEmpty()) {
            throw new NoDataException("Task not found");
        }
        return task.get();
    }

    public Task editDescription(UUID task_id, String description, UUID user_id) throws SQLException, NoDataException, NotAvailableException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        if (!checkPermission(task_id, user_id, jdbcSession)) {
            throw new NotAvailableException("You don't have permission to edit this task");
        }

        jdbcSession.sql("""
                UPDATE tasks
                SET description = ?
                WHERE id = ?
                """)
                .set(description)
                .set(task_id)
                .update(Outcome.VOID);
        Optional<Task> task = get(task_id);
        if (task.isEmpty()) {
            throw new NoDataException("Task not found");
        }
        return task.get();
    }

    public Task editPriority(UUID task_id, Priority priority, UUID user_id) throws SQLException, NoDataException, NotAvailableException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        if (!checkPermission(task_id, user_id, jdbcSession)) {
            throw new NotAvailableException("You don't have permission to edit this task");
        }

        jdbcSession.sql("""
                UPDATE tasks
                SET priority = CAST(? AS task_priority)
                WHERE id = ?
                """)
                .set(priority.toString())
                .set(task_id)
                .update(Outcome.VOID);
        Optional<Task> task = get(task_id);
        if (task.isEmpty()) {
            throw new NoDataException("Task not found");
        }
        return task.get();
    }

    public Task editStatus(UUID task_id, Status status, UUID user_id) throws SQLException, NoDataException, NotAvailableException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        boolean isAuthor = checkPermission(task_id, user_id, jdbcSession);

        Optional<UUID> taskExecutor = jdbcSession.sql("""
                SELECT user_id FROM user_executor_tasks
                WHERE task_id = ?
                """)
                .set(task_id)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Optional.of(UUID.fromString(rset.getString("user_id")));
                    } else {
                        return Optional.empty();
                    }
                });

        if (!isAuthor && (taskExecutor.isEmpty() || !taskExecutor.get().equals(user_id))) {
            throw new NotAvailableException("You don't have permission to edit this task");
        }

        jdbcSession.sql("""
                UPDATE tasks
                SET status = CAST(? AS task_status)
                WHERE id = ?
                """)
                .set(status.toString())
                .set(task_id)
                .update(Outcome.VOID);
        Optional<Task> task = get(task_id);
        if (task.isEmpty()) {
            throw new NoDataException("Task not found");
        }
        return task.get();
    }
}
