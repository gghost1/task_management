package com.example.endpoint;


import com.example.data.request.PaginationEntityRequest;
import com.example.data.entity.user.RpUser;
import com.example.data.entity.user.User;
import com.example.configuration.exception.NoDataException;
import com.example.data.response.BasicResponse;
import com.example.data.response.entityResponse.TaskEntityResponse;
import com.example.data.response.entityResponse.UserEntityResponse;
import com.example.configuration.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
@Tag(name = "Users", description = "API for user operations")
public class UserEndPoints {

    RpUser rpUser;
    JwtUtils jwtUtils;

    @GetMapping("/")
    @Operation(summary = "Get all users with pagination", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getUsers(@Valid PaginationEntityRequest paginationEntityRequest, HttpServletRequest request) throws SQLException {
        String jwt = jwtUtils.getJwtToken(request);

        return ResponseEntity.ok(new BasicResponse<>(jwt, UserEntityResponse.from(rpUser.getAll(paginationEntityRequest))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specified user by id", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getUser(@PathVariable UUID id, HttpServletRequest request) throws SQLException, NoDataException {
        String jwt = jwtUtils.getJwtToken(request);
        Optional<User> user = rpUser.get(id);
        if (user.isEmpty()) {
            throw new NoDataException("User not found");
        }

        return ResponseEntity.ok(new BasicResponse<>(jwt, UserEntityResponse.from(user.get())));
    }

    @GetMapping("/{id}/createdTasks")
    @Operation(summary = "Get tasks created by specified user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getUserTasks(@PathVariable UUID id, @Valid PaginationEntityRequest paginationEntityRequest, HttpServletRequest request) throws SQLException, NoDataException {
        String jwt = jwtUtils.getJwtToken(request);
        Optional<User> user = rpUser.get(id);
        if (user.isEmpty()) {
            throw new NoDataException("User not found");
        }

        return ResponseEntity.ok(new BasicResponse<>(jwt, TaskEntityResponse.from(user.get().createdTasks(paginationEntityRequest))));
    }

    @GetMapping("/{id}/assignedTasks")
    @Operation(summary = "Get tasks assigned to specified user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getAssignedTasks(@PathVariable UUID id, @Valid PaginationEntityRequest paginationEntityRequest, HttpServletRequest request) throws SQLException, NoDataException {
        String jwt = jwtUtils.getJwtToken(request);
        Optional<User> user = rpUser.get(id);
        if (user.isEmpty()) {
            throw new NoDataException("User not found");
        }

        return ResponseEntity.ok(new BasicResponse<>(jwt, TaskEntityResponse.from(user.get().assignedTasks(paginationEntityRequest))));
    }
}
