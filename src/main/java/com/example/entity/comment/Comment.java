package com.example.entity.comment;

import com.example.entity.user.User;
import com.example.exceptions.NoDataException;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public interface Comment {
    UUID id();
    String text();
    UUID authorId();
    Optional<User> author() throws SQLException;
}
