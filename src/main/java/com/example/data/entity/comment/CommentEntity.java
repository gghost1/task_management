package com.example.data.entity.comment;

import com.example.data.entity.user.RpUser;
import com.example.data.entity.user.User;

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
