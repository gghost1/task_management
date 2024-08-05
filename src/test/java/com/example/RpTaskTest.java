package com.example;

import com.example.db.InitializedTestContainer;
import com.example.dto.CommentDto;
import com.example.dto.TaskDto;
import com.example.entity.comment.Comment;
import com.example.entity.comment.RpComment;
import com.example.entity.elements.Priority;
import com.example.entity.elements.Status;
import com.example.entity.task.RpTask;
import com.example.entity.task.Task;
import com.example.entity.task.TaskEntity;
import com.example.entity.user.RpUser;
import com.example.exceptions.NoDataException;
import com.example.exceptions.NotAvailableException;
import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.SingleOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RpTaskTest extends InitializedTestContainer {
    @Autowired
    private DataSource dataSource;

    private RpComment rpComment;
    private RpUser rpUser;
    private RpTask rpTask;

    @BeforeEach
    public void init() {
        rpUser = new RpUser(dataSource);
        rpTask = new RpTask(dataSource);
        rpComment = new RpComment(dataSource);
    }


//    @Test
//    public void addTaskTest() throws SQLException, NoDataException {
//        UUID user_id;
//
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        try {
//            user_id = jdbcSession.sql("""
//                    INSERT INTO users (email, password)
//                    VALUES (?, ?)
//                    """)
//                    .set("test1")
//                    .set("test1")
//                    .insert(new SingleOutcome<>(UUID.class));
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//
//        Task task = rpTask.add(taskDto);
//
//        Task expected = jdbcSession.sql("SELECT * FROM tasks WHERE id = ?")
//                .set(task.id())
//                .select((rset, stmt) -> {
//                    if (rset.next()) {
//                        return new TaskEntity(
//                                UUID.fromString(rset.getString("id")),
//                                rset.getString("title"),
//                                rset.getString("description"),
//                                Status.valueOf(rset.getString("status")),
//                                Priority.valueOf(rset.getString("priority").toLowerCase()),
//                                dataSource
//                        );
//                    } else {
//                        throw new SQLException("No data found for the given ID");
//                    }
//                });
//
//        assertEquals(expected, task);
//
//        assertEquals(user_id, jdbcSession.sql("""
//                SELECT user_id FROM user_author_tasks
//                WHERE task_id = ?
//                """)
//                .set(task.id())
//                .select((rset, stmt) -> {
//                    if (rset.next()) {
//                        return UUID.fromString(rset.getString("user_id"));
//                    } else {
//                        throw new SQLException("No data found for the given ID");
//                    }
//                }));
//    }
//
//    @Test
//    public void getTaskTest() throws SQLException, NoDataException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        Optional<Task> expected = rpTask.get(task.id());
//
//        if (expected.isEmpty()) {
//            throw new NoDataException("No data found for the given ID");
//        }
//
//        assertEquals(expected.get(), task);
//    }
//
//    @Test
//    public void assignTaskTest() throws SQLException, NoDataException, NotAvailableException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        UUID user_id1 = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test2")
//                .set("test2")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//
//        rpTask.assign(task.id(), user_id1, user_id);
//
//
//        assertEquals(user_id, jdbcSession.sql("""
//                SELECT user_id FROM user_author_tasks
//                WHERE task_id = ?
//                """)
//                .set(task.id())
//                .select((rset, stmt) -> {
//                    if (rset.next()) {
//                        return UUID.fromString(rset.getString("user_id"));
//                    } else {
//                        throw new SQLException("No data found for the given ID");
//                    }
//                }));
//
//    }
//    @Test
//    public void assignTaskNegativeTest() throws SQLException, NoDataException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//
//        UUID user_id1 = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test2")
//                .set("test2")
//                .insert(new SingleOutcome<>(UUID.class));
//
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//
//        try {
//            rpTask.assign(task.id(), user_id1, user_id1);
//        } catch (NotAvailableException e) {
//            assertEquals("You don't have permission to assign this task", e.getMessage());
//        }
//    }
//
//    @Test
//    void deleteTaskTest() throws SQLException, NoDataException, NotAvailableException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        rpTask.delete(task.id(), user_id);
//        assertEquals(Optional.empty(), rpTask.get(task.id()));
//    }
//    @Test
//    void deleteTaskNegativeTest() throws SQLException, NoDataException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        UUID user_id1 = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test2")
//                .set("test2")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        try {
//            rpTask.delete(task.id(), user_id1);
//        } catch (NotAvailableException e) {
//            assertEquals("You don't have permission to delete this task", e.getMessage());
//        }
//        assertEquals(task, rpTask.get(task.id()).get());
//    }
//
//    @Test
//    void editTitleTest() throws SQLException, NoDataException, NotAvailableException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        rpTask.editTitle(task.id(), "NEW TITLE", user_id);
//        assertEquals("NEW TITLE", jdbcSession.sql("""
//                        SELECT title FROM tasks
//                        WHERE id = ?
//                        """)
//                .set(task.id())
//                .select((rset, stmt) -> {
//                    if (rset.next()) {
//                        return rset.getString("title");
//                    } else {
//                        throw new SQLException("No data found for the given ID");
//                    }
//                }));
//        assertEquals(task.description(), jdbcSession.sql("""
//                        SELECT description FROM tasks
//                        WHERE id = ?
//                        """)
//                .set(task.id())
//                .select((rset, stmt) -> {
//                    if (rset.next()) {
//                        return rset.getString("description");
//                    } else {
//                        throw new SQLException("No data found for the given ID");
//                    }
//                }));
//    }
//    @Test
//    void editTitleNegativeTest() throws SQLException, NoDataException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        UUID user_id1 = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        try {
//            rpTask.editTitle(task.id(), "NEW TITLE", user_id1);
//        } catch (NotAvailableException e) {
//            assertEquals("You don't have permission to edit this task", e.getMessage());
//        }
//        assertEquals(task, rpTask.get(task.id()).get());
//    }
//
//    @Test
//    void editDescriptionTest() throws SQLException, NoDataException, NotAvailableException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        rpTask.editDescription(task.id(), "NEW Description", user_id);
//        assertEquals("NEW Description", jdbcSession.sql("""
//                        SELECT description FROM tasks
//                        WHERE id = ?
//                        """)
//                .set(task.id())
//                .select((rset, stmt) -> {
//                    if (rset.next()) {
//                        return rset.getString("description");
//                    } else {
//                        throw new SQLException("No data found for the given ID");
//                    }
//                }));
//        assertEquals(task.title(), jdbcSession.sql("""
//                        SELECT title FROM tasks
//                        WHERE id = ?
//                        """)
//                .set(task.id())
//                .select((rset, stmt) -> {
//                    if (rset.next()) {
//                        return rset.getString("title");
//                    } else {
//                        throw new SQLException("No data found for the given ID");
//                    }
//                }));
//    }
//    @Test
//    void editDescriptionNegativeTest() throws SQLException, NoDataException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        UUID user_id1 = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        try {
//            rpTask.editDescription(task.id(), "NEW Description", user_id1);
//        } catch (NotAvailableException e) {
//            assertEquals("You don't have permission to edit this task", e.getMessage());
//        }
//        assertEquals(task, rpTask.get(task.id()).get());
//    }
//
//    @Test
//    void editPriorityTest() throws SQLException, NoDataException, NotAvailableException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        rpTask.editPriority(task.id(), Priority.medium, user_id);
//        assertEquals(Priority.medium, jdbcSession.sql("""
//                        SELECT priority FROM tasks
//                        WHERE id = ?
//                        """)
//                .set(task.id())
//                .select((rset, stmt) -> {
//                    if (rset.next()) {
//                        return Priority.valueOf(rset.getString("priority"));
//                    } else {
//                        throw new SQLException("No data found for the given ID");
//                    }
//                }));
//        assertEquals(task.title(), jdbcSession.sql("""
//                        SELECT title FROM tasks
//                        WHERE id = ?
//                        """)
//                .set(task.id())
//                .select((rset, stmt) -> {
//                    if (rset.next()) {
//                        return rset.getString("title");
//                    } else {
//                        throw new SQLException("No data found for the given ID");
//                    }
//                }));
//    }
//    @Test
//    void editPriorityNegativeTest() throws SQLException, NoDataException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        UUID user_id1 = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        try {
//            rpTask.editPriority(task.id(), Priority.medium, user_id1);
//        } catch (NotAvailableException e) {
//            assertEquals("You don't have permission to edit this task", e.getMessage());
//        }
//        assertEquals(task, rpTask.get(task.id()).get());
//    }
//
//    @Test
//    void editStatusTest() throws SQLException, NoDataException, NotAvailableException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        rpTask.editStatus(task.id(), Status.IN_PROGRESS, user_id);
//        assertEquals(Status.IN_PROGRESS, jdbcSession.sql("""
//                        SELECT status FROM tasks
//                        WHERE id = ?
//                        """)
//                .set(task.id())
//                .select((rset, stmt) -> {
//                    if (rset.next()) {
//                        return Status.valueOf(rset.getString("status"));
//                    } else {
//                        throw new SQLException("No data found for the given ID");
//                    }
//                }));
//        assertEquals(task.title(), jdbcSession.sql("""
//                        SELECT title FROM tasks
//                        WHERE id = ?
//                        """)
//                .set(task.id())
//                .select((rset, stmt) -> {
//                    if (rset.next()) {
//                        return rset.getString("title");
//                    } else {
//                        throw new SQLException("No data found for the given ID");
//                    }
//                }));
//    }
//    @Test
//    void editStatusNonNegativeTest() throws SQLException, NoDataException, NotAvailableException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        UUID user_id1 = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test2")
//                .set("test2")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        rpTask.assign(task.id(), user_id1, user_id);
//        rpTask.editStatus(task.id(), Status.IN_PROGRESS, user_id1);
//        assertEquals(Status.IN_PROGRESS, jdbcSession.sql("""
//                        SELECT status FROM tasks
//                        WHERE id = ?
//                        """)
//                .set(task.id())
//                .select((rset, stmt) -> {
//                    if (rset.next()) {
//                        return Status.valueOf(rset.getString("status"));
//                    } else {
//                        throw new SQLException("No data found for the given ID");
//                    }
//                }));
//        assertEquals(task.title(), jdbcSession.sql("""
//                        SELECT title FROM tasks
//                        WHERE id = ?
//                        """)
//                .set(task.id())
//                .select((rset, stmt) -> {
//                    if (rset.next()) {
//                        return rset.getString("title");
//                    } else {
//                        throw new SQLException("No data found for the given ID");
//                    }
//                }));
//    }
//    @Test
//    void editStatusNegativeTest() throws SQLException, NoDataException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        UUID user_id1 = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        try {
//            rpTask.editStatus(task.id(), Status.IN_PROGRESS, user_id1);
//        } catch (NotAvailableException e) {
//            assertEquals("You don't have permission to edit this task", e.getMessage());
//        }
//        assertEquals(task, rpTask.get(task.id()).get());
//    }
//
//    @Test
//    void getTaskAuthor() throws SQLException, NoDataException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        assertEquals(rpUser.get(user_id), task.creatorUser());
//    }
//
//    @Test
//    public void getTaskAssigned() throws SQLException, NoDataException, NotAvailableException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        UUID user_id1 = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test2")
//                .set("test2")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        rpTask.assign(task.id(), user_id1, user_id);
//        assertEquals(rpUser.get(user_id1), task.assignedUser());
//    }
//
//    @Test
//    void taskComments() throws SQLException, NoDataException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        UUID user_id = jdbcSession.sql("""
//                        INSERT INTO users (email, password)
//                        VALUES (?, ?)
//                        """)
//                .set("test1")
//                .set("test1")
//                .insert(new SingleOutcome<>(UUID.class));
//        TaskDto taskDto = new TaskDto(
//                null,
//                "test",
//                "test",
//                Status.NEW,
//                Priority.low,
//                user_id
//        );
//        Task task = rpTask.add(taskDto);
//        assertEquals(0, task.comments().size());
//
//        CommentDto commentDto1 = new CommentDto(
//                null,
//                "test",
//                task.id(),
//                user_id
//        );
//        CommentDto commentDto2 = new CommentDto(
//                null,
//                "test2",
//                task.id(),
//                user_id
//        );
//        List<Comment> comments = new ArrayList<>();
//        comments.add(rpComment.add(commentDto1));
//        comments.add(rpComment.add(commentDto2));
//        assertEquals(comments, task.comments());
//
//    }


}
