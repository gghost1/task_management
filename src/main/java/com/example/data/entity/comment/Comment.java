package com.example.data.entity.comment;

import com.example.data.entity.user.User;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public interface Comment {
    UUID id();
    String text();
    UUID authorId();
    Optional<User> author() throws SQLException;
}
