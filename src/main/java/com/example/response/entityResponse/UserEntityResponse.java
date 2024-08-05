package com.example.response.entityResponse;

import com.example.entity.user.User;
import lombok.Data;

import java.util.List;

@Data
public class UserEntityResponse {

    public String id;
    public String email;

    private UserEntityResponse(User user) {
        this.id = user.id().toString();
        this.email = user.email();
    }

    static public UserEntityResponse from(User user) {
        return new UserEntityResponse(user);
    }
    static public List<UserEntityResponse> from(List<User> user) {
        return user.stream().map(UserEntityResponse::new).toList();
    }
}
