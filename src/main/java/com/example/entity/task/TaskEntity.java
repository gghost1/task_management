package com.example.entity.task;

import com.example.dto.CommentDto;
import com.example.entity.comment.Comment;
import com.example.entity.comment.RpComment;
import com.example.entity.elements.Priority;
import com.example.entity.elements.Status;
import com.example.entity.user.User;
import com.example.entity.user.UserEntity;
import com.example.exceptions.NoDataException;
import com.example.exceptions.NotAvailableException;
import com.jcabi.jdbc.JdbcSession;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record TaskEntity (
    UUID id,
    String title,
    String description,
    Status status,
    Priority priority,
    DataSource dataSource
) implements Task {

    @Override
    public Optional<User> assignedUser() throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession.sql("""
                SELECT u.id, u.email, u.password
                FROM users u
                INNER JOIN user_executor_tasks uet ON u.id = uet.user_id
                WHERE uet.task_id = ?
                """)
                .set(id)
                .select((resultSet, statement) -> {
                    if (resultSet.next()) {
                        return Optional.of(new UserEntity(
                                UUID.fromString(resultSet.getString("id")),
                                resultSet.getString("email"),
                                resultSet.getString("password"),
                                dataSource
                        ));
                    }
                    return Optional.empty();
                });
    }

    @Override
    public Optional<User> creatorUser() throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession.sql("""
                SELECT u.id, u.email, u.password
                FROM users u
                INNER JOIN user_author_tasks uet ON u.id = uet.user_id
                WHERE uet.task_id = ?
                """)
                .set(id)
                .select((resultSet, statement) -> {
                    if (resultSet.next()) {
                        return Optional.of(new UserEntity(
                                UUID.fromString(resultSet.getString("id")),
                                resultSet.getString("email"),
                                resultSet.getString("password"),
                                dataSource
                        ));
                    }
                    return Optional.empty();
                });
    }

    @Override
    public List<Comment> comments() throws SQLException {
        RpComment rpComment = new RpComment(dataSource);
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        List<UUID> commentIds = jdbcSession.sql("""
                SELECT comment_id FROM task_comments
                WHERE task_id = ?
                """)
                .set(id)
                .select((rset, stmt) -> {
                    List<UUID> ids = new ArrayList<>();
                    while (rset.next()) {
                        ids.add(UUID.fromString(rset.getString("comment_id")));
                    }
                    return ids;
                });
        return commentIds.stream().map(commentId -> {
            try {
                Optional<Comment> comment = rpComment.get(commentId);
                return comment.orElse(null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

}
