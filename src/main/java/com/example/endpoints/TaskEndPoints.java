package com.example.endpoints;


import com.example.dto.CommentDto;
import com.example.entity.comment.RpComment;
import com.example.entity.elements.Priority;
import com.example.entity.elements.Status;
import com.example.entity.task.RpTask;
import com.example.entity.task.Task;
import com.example.entity.user.RpUser;
import com.example.exceptions.NoDataException;
import com.example.exceptions.NotAvailableException;
import com.example.exceptions.NotValidParam;
import com.example.response.BasicResponce;
import com.example.response.entityResponse.TaskEntityResponse;
import com.example.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskEndPoints {

    RpTask rpTask;
    RpUser rpUser;
    JwtUtils jwtUtils;
    RpComment rpComment;

    @GetMapping("/")
    public ResponseEntity<?> getTasks(HttpServletRequest request) throws SQLException {
        String jwt = jwtUtils.getJwtToken(request);

        return ResponseEntity.ok(new BasicResponce<>(jwt, TaskEntityResponse.from(rpTask.getAll())));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable UUID id, HttpServletRequest request) throws SQLException, NoDataException {
        String jwt = jwtUtils.getJwtToken(request);

        Optional<Task> task = rpTask.get(id);
        if (task.isEmpty()) {
            throw new NoDataException("Task not found");
        }

        return ResponseEntity.ok(new BasicResponce<>(jwt, TaskEntityResponse.from(task.get())));
    }
    @PostMapping("/{id}/addComment")
    public ResponseEntity<?> addComment(@PathVariable UUID id, @Valid @RequestBody CommentDto commentDto, HttpServletRequest request) throws SQLException, NoDataException {
        String jwt = jwtUtils.getJwtToken(request);
        rpComment.add(commentDto, UUID.fromString(jwtUtils.getIdFromJwtToken(jwt)), id);
        Optional<Task> task = rpTask.get(id);
        if (task.isEmpty()) {
            throw new NoDataException("Task not found");
        }
        return ResponseEntity.ok(new BasicResponce<>(jwt, TaskEntityResponse.from(task.get())));
    }
    @PostMapping("/{id}/assign")
    public ResponseEntity<?> assign(@PathVariable UUID id, @RequestBody UUID userToAssign_id, HttpServletRequest request) throws SQLException, NoDataException, NotAvailableException {
        String jwt = jwtUtils.getJwtToken(request);
        Task task = rpTask.assign(id, userToAssign_id, UUID.fromString(jwtUtils.getIdFromJwtToken(jwt)));
        return ResponseEntity.ok(new BasicResponce<>(jwt, TaskEntityResponse.from(task)));
    }

    @PostMapping("/{id}/editTitle")
    public ResponseEntity<?> editTitle(@PathVariable UUID id, @RequestBody String title, HttpServletRequest request) throws SQLException, NoDataException, NotAvailableException {
        String jwt = jwtUtils.getJwtToken(request);
        Task task = rpTask.editTitle(id, title, UUID.fromString(jwtUtils.getIdFromJwtToken(jwt)));
        return ResponseEntity.ok(new BasicResponce<>(jwt, TaskEntityResponse.from(task)));
    }
    @PostMapping("/{id}/editDescription")
    public ResponseEntity<?> editDescription(@PathVariable UUID id, @RequestBody String description, HttpServletRequest request) throws SQLException, NoDataException, NotAvailableException {
        String jwt = jwtUtils.getJwtToken(request);
        Task task = rpTask.editDescription(id, description, UUID.fromString(jwtUtils.getIdFromJwtToken(jwt)));
        return ResponseEntity.ok(new BasicResponce<>(jwt, TaskEntityResponse.from(task)));
    }
    @PostMapping("/{id}/editStatus")
    public ResponseEntity<?> editStatus(@PathVariable UUID id, @RequestBody String status, HttpServletRequest request) throws SQLException, NoDataException, NotAvailableException {
        String jwt = jwtUtils.getJwtToken(request);
        try {
            Status.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new NoDataException("Status not found");
        }
        Task task = rpTask.editStatus(id, Status.valueOf(status), UUID.fromString(jwtUtils.getIdFromJwtToken(jwt)));
        return ResponseEntity.ok(new BasicResponce<>(jwt, TaskEntityResponse.from(task)));
    }
    @PostMapping("/{id}/editPriority")
    public ResponseEntity<?> editPriority(@PathVariable UUID id, @RequestBody String priority, HttpServletRequest request) throws SQLException, NoDataException, NotAvailableException {
        String jwt = jwtUtils.getJwtToken(request);
        try {
            Priority.valueOf(priority);
        } catch (IllegalArgumentException e) {
            throw new NoDataException("Priority not found");
        }
        Task task = rpTask.editPriority(id, Priority.valueOf(priority), UUID.fromString(jwtUtils.getIdFromJwtToken(jwt)));
        return ResponseEntity.ok(new BasicResponce<>(jwt, TaskEntityResponse.from(task)));
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable UUID id, HttpServletRequest request) throws SQLException, NoDataException, NotAvailableException {
        String jwt = jwtUtils.getJwtToken(request);
        rpTask.delete(id, UUID.fromString(jwtUtils.getIdFromJwtToken(jwt)));
        return ResponseEntity.ok(new BasicResponce<>(jwt, "Task deleted successfully"));
    }

}
