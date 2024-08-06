package com.example.endpoints;

import com.example.dto.TaskDto;
import com.example.entity.task.RpTask;
import com.example.entity.task.Task;
import com.example.entity.user.RpUser;
import com.example.exceptions.NoDataException;
import com.example.response.BasicResponce;
import com.example.response.entityResponse.TaskEntityResponse;
import com.example.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.UUID;

@RestController
@RequestMapping("/")
@AllArgsConstructor
@Tag(name = "Basic", description = "Basic operations")
public class HomeEndPoints {
    RpTask rpTask;
    RpUser rpUser;
    JwtUtils jwtUtils;

    @PostMapping("/createTask")
    @Operation(summary = "Create task by user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskDto taskDto, HttpServletRequest request) throws SQLException, NoDataException {
        String jwt = request.getHeader("Authorization").substring(7);
        UUID id = UUID.fromString(jwtUtils
                .getIdFromJwtToken(
                        request.getHeader("Authorization")
                                .substring(7)
                ));

        Task task = rpTask.add(taskDto, id);

        return ResponseEntity.ok(new BasicResponce<>(jwt, TaskEntityResponse.from(task)));
    }

}
