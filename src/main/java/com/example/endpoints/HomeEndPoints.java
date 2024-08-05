package com.example.endpoints;

import com.example.dto.TaskDto;
import com.example.entity.task.RpTask;
import com.example.entity.user.RpUser;
import com.example.exceptions.NoDataException;
import com.example.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.UUID;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class HomeEndPoints {
    RpTask rpTask;
    RpUser rpUser;
    JwtUtils jwtUtils;

    @PostMapping("/createTask")
    public ResponseEntity<?> createTask(@RequestBody TaskDto taskDto, HttpServletRequest request) throws SQLException, NoDataException {
        String jwt = request.getHeader("Authorization").substring(7);
        UUID id = UUID.fromString(jwtUtils
                .getIdFromJwtToken(
                        request.getHeader("Authorization")
                                .substring(7)
                ));

        rpTask.add(taskDto, id);

        return ResponseEntity.ok(jwt);
    }

}
