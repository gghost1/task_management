package com.example.entity.task;

import com.example.dto.CommentDto;
import com.example.entity.comment.Comment;
import com.example.entity.elements.Priority;
import com.example.entity.elements.Status;
import com.example.entity.user.User;
import com.example.exceptions.NoDataException;
import com.example.exceptions.NotAvailableException;

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
