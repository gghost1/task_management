package com.example.endpoints;


import com.example.entity.user.RpUser;
import com.example.entity.user.User;
import com.example.exceptions.NoDataException;
import com.example.response.BasicResponce;
import com.example.response.entityResponse.TaskEntityResponse;
import com.example.response.entityResponse.UserEntityResponse;
import com.example.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserEndPoints {

    RpUser rpUser;
    JwtUtils jwtUtils;


    @GetMapping("/")
    public ResponseEntity<?> getUsers(HttpServletRequest request) throws SQLException {
        String jwt = jwtUtils.getJwtToken(request);
        return ResponseEntity.ok(new BasicResponce<>(jwt, UserEntityResponse.from(rpUser.getAll())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable UUID id, HttpServletRequest request) throws SQLException, NoDataException {
        String jwt = jwtUtils.getJwtToken(request);
        Optional<User> user = rpUser.get(id);
        if (user.isEmpty()) {
            throw new NoDataException("User not found");
        }

        return ResponseEntity.ok(new BasicResponce<>(jwt, UserEntityResponse.from(user.get())));
    }
    @GetMapping("/{id}/createdTasks")
    public ResponseEntity<?> getUserTasks(@PathVariable UUID id, HttpServletRequest request) throws SQLException, NoDataException {
        String jwt = jwtUtils.getJwtToken(request);
        Optional<User> user = rpUser.get(id);
        if (user.isEmpty()) {
            throw new NoDataException("User not found");
        }

        return ResponseEntity.ok(new BasicResponce<>(jwt, TaskEntityResponse.from(user.get().createdTasks())));
    }
    @GetMapping("/{id}/assignedTasks")
    public ResponseEntity<?> getAssignedTasks(@PathVariable UUID id, HttpServletRequest request) throws SQLException, NoDataException {
        String jwt = jwtUtils.getJwtToken(request);
        Optional<User> user = rpUser.get(id);
        if (user.isEmpty()) {
            throw new NoDataException("User not found");
        }

        return ResponseEntity.ok(new BasicResponce<>(jwt, TaskEntityResponse.from(user.get().assignedTasks())));
    }

}
