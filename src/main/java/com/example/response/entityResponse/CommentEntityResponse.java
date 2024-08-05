package com.example.response.entityResponse;

import com.example.entity.comment.Comment;
import com.example.entity.user.User;
import com.example.exceptions.NoDataException;
import lombok.Data;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
public class CommentEntityResponse {

    public UUID id;
    public String text;
    public UserEntityResponse user;

    private CommentEntityResponse(Comment comment) throws SQLException {
        this.id = comment.id();
        this.text = comment.text();

        Optional<User> user = comment.author();
        user.ifPresent(value -> this.user = UserEntityResponse.from(value));
    }


    public static List<CommentEntityResponse> from(List<Comment> comments) throws SQLException {
        return comments.stream().map(comment -> {
            try {
                return new CommentEntityResponse(comment);
            } catch (SQLException e) {
                return null;
            }
        }).toList();
    }

    public static CommentEntityResponse from(Comment comment) throws SQLException {
        return new CommentEntityResponse(comment);
    }

}
