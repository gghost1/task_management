package com.example.endpoint;

import com.example.data.request.TaskEntityRequest;
import com.example.data.entity.task.RpTask;
import com.example.data.entity.task.Task;
import com.example.data.entity.user.RpUser;
import com.example.configuration.exception.NoDataException;
import com.example.data.response.BasicResponse;
import com.example.data.response.entityResponse.TaskEntityResponse;
import com.example.configuration.security.JwtUtils;
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
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskEntityRequest taskEntityRequest, HttpServletRequest request) throws SQLException, NoDataException {
        String jwt = request.getHeader("Authorization").substring(7);
        UUID id = UUID.fromString(jwtUtils
                .getIdFromJwtToken(
                        request.getHeader("Authorization")
                                .substring(7)
                ));

        Task task = rpTask.add(taskEntityRequest, id);

        return ResponseEntity.ok(new BasicResponse<>(jwt, TaskEntityResponse.from(task)));
    }
}
