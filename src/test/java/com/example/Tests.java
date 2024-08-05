package com.example;

import com.example.db.InitializedTestContainer;
import com.example.dto.CommentDto;
import com.example.dto.TaskDto;
import com.example.dto.UserDto;
import com.example.entity.comment.Comment;
import com.example.entity.comment.CommentEntity;
import com.example.entity.comment.RpComment;
import com.example.entity.elements.Priority;
import com.example.entity.elements.Status;
import com.example.entity.task.RpTask;
import com.example.entity.task.Task;
import com.example.entity.task.TaskEntity;
import com.example.entity.user.RpUser;
import com.example.entity.user.User;
import com.example.entity.user.UserEntity;
import com.example.exceptions.NoDataException;
import com.example.exceptions.NotAvailableException;
import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.SingleOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class Tests extends InitializedTestContainer {
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

    @Test
    public void addTaskTest() throws SQLException, NoDataException {
        UUID user_id;

        JdbcSession jdbcSession = new JdbcSession(dataSource);
        try {
            user_id = jdbcSession.sql("""
                    INSERT INTO users (email, password)
                    VALUES (?, ?)
                    """)
                    .set("test1")
                    .set("test1")
                    .insert(new SingleOutcome<>(UUID.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );

        Task task = rpTask.add(taskDto, user_id);

        Task expected = jdbcSession.sql("SELECT * FROM tasks WHERE id = ?")
                .set(task.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return new TaskEntity(
                                UUID.fromString(rset.getString("id")),
                                rset.getString("title"),
                                rset.getString("description"),
                                Status.valueOf(rset.getString("status")),
                                Priority.valueOf(rset.getString("priority").toLowerCase()),
                                dataSource
                        );
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                });

        assertEquals(expected, task);

        assertEquals(user_id, jdbcSession.sql("""
                SELECT user_id FROM user_author_tasks
                WHERE task_id = ?
                """)
                .set(task.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return UUID.fromString(rset.getString("user_id"));
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                }));
    }

    @Test
    public void getTaskTest() throws SQLException, NoDataException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        Optional<Task> expected = rpTask.get(task.id());

        if (expected.isEmpty()) {
            throw new NoDataException("No data found for the given ID");
        }

        assertEquals(expected.get(), task);
    }

    @Test
    public void assignTaskTest() throws SQLException, NoDataException, NotAvailableException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        UUID user_id1 = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test2")
                .set("test2")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);

        rpTask.assign(task.id(), user_id1, user_id);


        assertEquals(user_id, jdbcSession.sql("""
                SELECT user_id FROM user_author_tasks
                WHERE task_id = ?
                """)
                .set(task.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return UUID.fromString(rset.getString("user_id"));
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                }));

    }
    @Test
    public void assignTaskNegativeTest() throws SQLException, NoDataException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));

        UUID user_id1 = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test2")
                .set("test2")
                .insert(new SingleOutcome<>(UUID.class));

        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);

        try {
            rpTask.assign(task.id(), user_id1, user_id1);
        } catch (NotAvailableException e) {
            assertEquals("You don't have permission to assign this task", e.getMessage());
        }
    }

    @Test
    void deleteTaskTest() throws SQLException, NoDataException, NotAvailableException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));

        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        rpTask.delete(task.id(), user_id);
        assertEquals(Optional.empty(), rpTask.get(task.id()));
    }
    @Test
    void deleteTaskNegativeTest() throws SQLException, NoDataException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        UUID user_id1 = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test2")
                .set("test2")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        try {
            rpTask.delete(task.id(), user_id1);
        } catch (NotAvailableException e) {
            assertEquals("You don't have permission to delete this task", e.getMessage());
        }
        assertEquals(task, rpTask.get(task.id()).get());
    }

    @Test
    void editTitleTest() throws SQLException, NoDataException, NotAvailableException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        rpTask.editTitle(task.id(), "NEW TITLE", user_id);
        assertEquals("NEW TITLE", jdbcSession.sql("""
                        SELECT title FROM tasks
                        WHERE id = ?
                        """)
                .set(task.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return rset.getString("title");
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                }));
        assertEquals(task.description(), jdbcSession.sql("""
                        SELECT description FROM tasks
                        WHERE id = ?
                        """)
                .set(task.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return rset.getString("description");
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                }));
    }
    @Test
    void editTitleNegativeTest() throws SQLException, NoDataException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        UUID user_id1 = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        try {
            rpTask.editTitle(task.id(), "NEW TITLE", user_id1);
        } catch (NotAvailableException e) {
            assertEquals("You don't have permission to edit this task", e.getMessage());
        }
        assertEquals(task, rpTask.get(task.id()).get());
    }

    @Test
    void editDescriptionTest() throws SQLException, NoDataException, NotAvailableException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        rpTask.editDescription(task.id(), "NEW Description", user_id);
        assertEquals("NEW Description", jdbcSession.sql("""
                        SELECT description FROM tasks
                        WHERE id = ?
                        """)
                .set(task.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return rset.getString("description");
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                }));
        assertEquals(task.title(), jdbcSession.sql("""
                        SELECT title FROM tasks
                        WHERE id = ?
                        """)
                .set(task.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return rset.getString("title");
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                }));
    }
    @Test
    void editDescriptionNegativeTest() throws SQLException, NoDataException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        UUID user_id1 = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        try {
            rpTask.editDescription(task.id(), "NEW Description", user_id1);
        } catch (NotAvailableException e) {
            assertEquals("You don't have permission to edit this task", e.getMessage());
        }
        assertEquals(task, rpTask.get(task.id()).get());
    }

    @Test
    void editPriorityTest() throws SQLException, NoDataException, NotAvailableException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        rpTask.editPriority(task.id(), Priority.medium, user_id);
        assertEquals(Priority.medium, jdbcSession.sql("""
                        SELECT priority FROM tasks
                        WHERE id = ?
                        """)
                .set(task.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Priority.valueOf(rset.getString("priority"));
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                }));
        assertEquals(task.title(), jdbcSession.sql("""
                        SELECT title FROM tasks
                        WHERE id = ?
                        """)
                .set(task.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return rset.getString("title");
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                }));
    }
    @Test
    void editPriorityNegativeTest() throws SQLException, NoDataException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        UUID user_id1 = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        try {
            rpTask.editPriority(task.id(), Priority.medium, user_id1);
        } catch (NotAvailableException e) {
            assertEquals("You don't have permission to edit this task", e.getMessage());
        }
        assertEquals(task, rpTask.get(task.id()).get());
    }

    @Test
    void editStatusTest() throws SQLException, NoDataException, NotAvailableException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        rpTask.editStatus(task.id(), Status.IN_PROGRESS, user_id);
        assertEquals(Status.IN_PROGRESS, jdbcSession.sql("""
                        SELECT status FROM tasks
                        WHERE id = ?
                        """)
                .set(task.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Status.valueOf(rset.getString("status"));
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                }));
        assertEquals(task.title(), jdbcSession.sql("""
                        SELECT title FROM tasks
                        WHERE id = ?
                        """)
                .set(task.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return rset.getString("title");
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                }));
    }
    @Test
    void editStatusNonNegativeTest() throws SQLException, NoDataException, NotAvailableException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        UUID user_id1 = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test2")
                .set("test2")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        rpTask.assign(task.id(), user_id1, user_id);
        rpTask.editStatus(task.id(), Status.IN_PROGRESS, user_id1);
        assertEquals(Status.IN_PROGRESS, jdbcSession.sql("""
                        SELECT status FROM tasks
                        WHERE id = ?
                        """)
                .set(task.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Status.valueOf(rset.getString("status"));
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                }));
        assertEquals(task.title(), jdbcSession.sql("""
                        SELECT title FROM tasks
                        WHERE id = ?
                        """)
                .set(task.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return rset.getString("title");
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                }));
    }
    @Test
    void editStatusNegativeTest() throws SQLException, NoDataException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        UUID user_id1 = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        try {
            rpTask.editStatus(task.id(), Status.IN_PROGRESS, user_id1);
        } catch (NotAvailableException e) {
            assertEquals("You don't have permission to edit this task", e.getMessage());
        }
        assertEquals(task, rpTask.get(task.id()).get());
    }

    @Test
    void getTaskAuthor() throws SQLException, NoDataException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        assertEquals(rpUser.get(user_id), task.creatorUser());
    }

    @Test
    public void getTaskAssigned() throws SQLException, NoDataException, NotAvailableException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        UUID user_id1 = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test2")
                .set("test2")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        rpTask.assign(task.id(), user_id1, user_id);
        assertEquals(rpUser.get(user_id1), task.assignedUser());
    }

    @Test
    void taskComments() throws SQLException, NoDataException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID user_id = jdbcSession.sql("""
                        INSERT INTO users (email, password)
                        VALUES (?, ?)
                        """)
                .set("test1")
                .set("test1")
                .insert(new SingleOutcome<>(UUID.class));
        TaskDto taskDto = new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskDto, user_id);
        assertEquals(0, task.comments().size());

        CommentDto commentDto1 = new CommentDto(
                null,
                "test"
        );
        CommentDto commentDto2 = new CommentDto(
                null,
                "test2"
        );
        List<Comment> comments = new ArrayList<>();
        comments.add(rpComment.add(commentDto1, user_id, task.id()));
        comments.add(rpComment.add(commentDto2, user_id, task.id()));
        assertEquals(comments, task.comments());

    }

    @Test
    public void addCommentTest() throws SQLException, NoDataException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        rpUser = new RpUser(dataSource);
        UUID userId = rpUser.add(new UserDto(
                null,
                "test",
                "test"
        )).id();

        UUID taskId = rpTask.add(new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        ), userId).id();

        CommentDto commentDto = new CommentDto(
                null,
                "test"
        );

        Comment comment = rpComment.add(commentDto, userId, taskId);
        Comment expected = jdbcSession
                .sql("SELECT * FROM comments WHERE id = ?")
                .set(comment.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return new CommentEntity(
                                UUID.fromString(rset.getString("id")),
                                rset.getString("text"),
                                UUID.fromString(rset.getString("author_id")),
                                dataSource
                        );
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                });

        assertEquals(expected, comment);
    }

    @Test
    public void getCommentTest() throws SQLException, NoDataException {
        UUID userId = rpUser.add(new UserDto(
                null,
                "test",
                "test"
        )).id();

        UUID taskId = rpTask.add(new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        ),  userId).id();

        CommentDto commentDto = new CommentDto(
                null,
                "test"
        );

        Comment comment = rpComment.add(commentDto, userId, taskId);
        Optional<Comment> expected = rpComment.get(comment.id());
        assertEquals(expected.get(), comment);
    }

    @Test
    public void deleteAllForTaskTest() throws SQLException, NoDataException {
        UUID userId = rpUser.add(new UserDto(
                null,
                "test",
                "test"
        )).id();

        UUID taskId = rpTask.add(new TaskDto(
                null,
                "test",
                "test",
                Status.NEW,
                Priority.low
        ),  userId).id();

        CommentDto commentDto = new CommentDto(
                null,
                "test"
        );
        rpComment.add(commentDto, userId, taskId);
        rpComment.deleteAllForTask(taskId);

        Optional<Comment> comment = rpComment.get(commentDto.id());
        assertEquals(Optional.empty(), comment);
    }

    UserDto userDto = new UserDto(
            null,
            "test",
            "test"
    );

    @Test
    public void addUserTest() throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        User user = rpUser.add(userDto);

        User expected = jdbcSession.sql("""
                            SELECT id, email, password from users
                            WHERE id = ?
                            """)
                .set(user.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return new UserEntity(
                                UUID.fromString(rset.getString("id")),
                                rset.getString("email"),
                                rset.getString("password"),
                                rpUser.dataSource
                        );
                    } else {
                        throw new SQLException("No data found for the given ID");
                    }
                });
        assertEquals(expected, user);
    }

    @Test
    public void getUserTest() throws SQLException, NoDataException {
        User user = rpUser.add(userDto);
        Optional<User> expected = rpUser.get(user.id());

        if (expected.isEmpty()) {
            throw new NoDataException("No data found for the given ID");
        }

        assertEquals(user, expected.get());
    }

    @Test
    public void createdTasksTest() throws SQLException, NoDataException {
        User user = rpUser.add(userDto);
        assertEquals(0, user.createdTasks().size());
        TaskDto taskDto1 = new TaskDto(
                null,
                "test1",
                "test1",
                Status.NEW,
                Priority.medium
        );
        TaskDto taskDto2 = new TaskDto(
                null,
                "test2",
                "test2",
                Status.NEW,
                Priority.medium
        );
        List<Task> tasks = new ArrayList<>();

        tasks.add(rpTask.add(taskDto1, user.id()));
        tasks.add(rpTask.add(taskDto2, user.id()));

        assertEquals(tasks, user.createdTasks());

    }

    @Test
    public void assignedTasksTest() throws SQLException, NoDataException, NotAvailableException {
        User user = rpUser.add(userDto);
        User user1 = rpUser.add(userDto);
        TaskDto taskDto1 = new TaskDto(
                null,
                "test1",
                "test1",
                Status.NEW,
                Priority.medium
        );
        TaskDto taskDto2 = new TaskDto(
                null,
                "test2",
                "test2",
                Status.NEW,
                Priority.medium
        );
        TaskDto taskDto3 = new TaskDto(
                null,
                "test3",
                "test3",
                Status.NEW,
                Priority.medium
        );
        List<Task> tasks = new ArrayList<>();
        rpTask.add(taskDto3, user.id());
        tasks.add(rpTask.add(taskDto1, user.id()));
        tasks.add(rpTask.add(taskDto2, user.id()));

        rpTask.assign(tasks.get(0).id(), user1.id(), user.id());
        rpTask.assign(tasks.get(1).id(), user1.id(), user.id());

        assertEquals(tasks, user1.assignedTasks());
        assertEquals(0, user.assignedTasks().size());

    }

}
