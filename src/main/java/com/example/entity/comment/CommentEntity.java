package com.example.entity.comment;

import com.example.entity.user.RpUser;
import com.example.entity.user.User;
import com.example.exceptions.NoDataException;
import com.jcabi.jdbc.JdbcSession;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public record CommentEntity (
        UUID id,
        String text,
        UUID authorId,
        DataSource dataSource
) implements Comment {

    @Override
    public Optional<User> author() throws SQLException {
        RpUser rpUser = new RpUser(dataSource);
        return rpUser.get(authorId);
    }
}
