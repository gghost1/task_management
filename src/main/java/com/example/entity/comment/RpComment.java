package com.example.entity.comment;

import com.example.dto.CommentDto;
import com.example.entity.elements.Priority;
import com.example.entity.elements.Status;
import com.example.entity.task.RpTask;
import com.example.entity.task.Task;
import com.example.entity.task.TaskEntity;
import com.example.entity.user.RpUser;
import com.example.entity.user.User;
import com.example.entity.user.UserEntity;
import com.example.exceptions.NoDataException;
import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Outcome;
import com.jcabi.jdbc.SingleOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RpComment {

    private final DataSource dataSource;


    public RpComment(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Comment add(CommentDto comment, UUID user_id, UUID task_id) throws SQLException, NoDataException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        RpUser rpUser = new RpUser(dataSource);
        Optional<User> user = rpUser.get(user_id);
        if (user.isEmpty()) {
            throw new NoDataException("User not found");
        }
        RpTask rpTask = new RpTask(dataSource);
        Optional<Task> task = rpTask.get(task_id);
        if (task.isEmpty()) {
            throw new NoDataException("Task not found");
        }

        UUID id = jdbcSession
                .sql("""
                        INSERT INTO comments (text, author_id)
                        VALUES (?, ?)
                        """)
                .set(comment.text())
                .set(user_id)
                .insert(new SingleOutcome<>(UUID.class));
        jdbcSession.sql("""
                INSERT INTO task_comments (comment_id, task_id)
                VALUES (?, ?)
                """)
                .set(id)
                .set(task_id)
                .insert(Outcome.VOID);
        return new CommentEntity(
                id,
                comment.text(),
                user_id,
                dataSource
        );
    }

    public Optional<Comment> get(UUID id) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT id, text, author_id FROM comments
                        WHERE id = ?
                        """)
                .set(id)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Optional.of(new CommentEntity(
                                UUID.fromString(rset.getString("id")),
                                rset.getString("text"),
                                UUID.fromString(rset.getString("author_id")),
                                dataSource
                        ));
                    } else {
                        return Optional.empty();
                    }
                });
    }

    public void deleteAllForTask(UUID task_id) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        List<UUID> commentIds = jdbcSession.sql("""
                SELECT comment_id FROM task_comments
                WHERE task_id = ?
                """)
                .set(task_id)
                .select((rset, stmt) -> {
                    List<UUID> ids = new ArrayList<>();
                    while (rset.next()) {
                        ids.add(UUID.fromString(rset.getString("comment_id")));
                    }
                    return ids;
                });
        jdbcSession.sql("""
                DELETE FROM task_comments
                WHERE task_id = ?
                """)
                .set(task_id)
                .update(Outcome.VOID);

        commentIds.forEach(id -> {
            try {
                jdbcSession
                        .sql("""
                            DELETE FROM comments
                            WHERE id = ?
                            """)
                        .set(id)
                        .update(Outcome.VOID);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
