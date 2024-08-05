package com.example.response.entityResponse;

import com.example.entity.comment.Comment;
import com.example.entity.elements.Priority;
import com.example.entity.elements.Status;
import com.example.entity.task.Task;
import com.example.entity.user.User;
import lombok.Data;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
public class TaskEntityResponse {
    UUID id;
    String title;
    String description;
    Status status;
    Priority priority;
    UserEntityResponse creator;
    UserEntityResponse assignedUser;
    List<CommentEntityResponse> comments;

    private TaskEntityResponse(Task task) throws SQLException {
        this.id = task.id();
        this.title = task.title();
        this.description = task.description();
        this.status = task.status();
        this.priority = task.priority();


        Optional<User> creatorUser = task.creatorUser();
        creatorUser.ifPresent(value -> this.creator = UserEntityResponse.from(value));

        Optional<User> assignedUser = task.assignedUser();
        assignedUser.ifPresent(value -> this.assignedUser = UserEntityResponse.from(value));

        this.comments = CommentEntityResponse.from(task.comments());
    }

    public static List<TaskEntityResponse> from(List<Task> tasks) {
        return tasks.stream().map(task -> {
            try {
                return new TaskEntityResponse(task);
            } catch (SQLException e) {
                return null;
            }
        }).toList();
    }

    public static TaskEntityResponse from(Task task) throws SQLException {
        return new TaskEntityResponse(task);
    }

}
