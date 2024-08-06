package com.example.data.entity.task;

import com.example.data.entity.user.User;
import com.example.data.entity.comment.Comment;
import com.example.data.entity.elements.Priority;
import com.example.data.entity.elements.Status;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Task {
    UUID id();
    String title();
    String description();
    Status status();
    Priority priority();
    Optional<User> assignedUser() throws SQLException;
    Optional<User> creatorUser() throws SQLException;
    List<Comment> comments() throws SQLException;
}
