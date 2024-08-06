package com.example;

import com.example.db.InitializedTestContainer;
import com.example.data.request.CommentEntityRequest;
import com.example.data.request.PaginationEntityRequest;
import com.example.data.request.TaskEntityRequest;
import com.example.data.request.UserEntityRequest;
import com.example.data.entity.comment.Comment;
import com.example.data.entity.comment.CommentEntity;
import com.example.data.entity.comment.RpComment;
import com.example.data.entity.elements.Priority;
import com.example.data.entity.elements.Status;
import com.example.data.entity.task.RpTask;
import com.example.data.entity.task.Task;
import com.example.data.entity.task.TaskEntity;
import com.example.data.entity.user.RpUser;
import com.example.data.entity.user.User;
import com.example.data.entity.user.UserEntity;
import com.example.configuration.exception.NoDataException;
import com.example.configuration.exception.NotAvailableException;
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
    private PaginationEntityRequest paginationEntityRequest;

    @BeforeEach
    public void init() {
        rpUser = new RpUser(dataSource);
        rpTask = new RpTask(dataSource);
        rpComment = new RpComment(dataSource);
        paginationEntityRequest = new PaginationEntityRequest(0, 5);
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

        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );

        Task task = rpTask.add(taskEntityRequest, user_id);

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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);

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

        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);

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

        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
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
        TaskEntityRequest taskEntityRequest = new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        );
        Task task = rpTask.add(taskEntityRequest, user_id);
        assertEquals(0, task.comments().size());

        CommentEntityRequest commentEntityRequest1 = new CommentEntityRequest(
                "test"
        );
        CommentEntityRequest commentEntityRequest2 = new CommentEntityRequest(
                "test2"
        );
        List<Comment> comments = new ArrayList<>();
        comments.add(rpComment.add(commentEntityRequest1, user_id, task.id()));
        comments.add(rpComment.add(commentEntityRequest2, user_id, task.id()));
        assertEquals(comments, task.comments());

    }

    @Test
    public void addCommentTest() throws SQLException, NoDataException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        rpUser = new RpUser(dataSource);
        UUID userId = rpUser.add(new UserEntityRequest(
                "test",
                "test"
        )).id();

        UUID taskId = rpTask.add(new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        ), userId).id();

        CommentEntityRequest commentEntityRequest = new CommentEntityRequest(
                "test"
        );

        Comment comment = rpComment.add(commentEntityRequest, userId, taskId);
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
        UUID userId = rpUser.add(new UserEntityRequest(
                "test",
                "test"
        )).id();

        UUID taskId = rpTask.add(new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        ),  userId).id();

        CommentEntityRequest commentEntityRequest = new CommentEntityRequest(
                "test"
        );

        Comment comment = rpComment.add(commentEntityRequest, userId, taskId);
        Optional<Comment> expected = rpComment.get(comment.id());
        assertEquals(expected.get(), comment);
    }

    @Test
    public void deleteAllForTaskTest() throws SQLException, NoDataException {
        UUID userId = rpUser.add(new UserEntityRequest(
                "test",
                "test"
        )).id();

        UUID taskId = rpTask.add(new TaskEntityRequest(
                "test",
                "test",
                Status.NEW,
                Priority.low
        ),  userId).id();

        CommentEntityRequest commentEntityRequest = new CommentEntityRequest(
                "test"
        );
        Comment commentAdd = rpComment.add(commentEntityRequest, userId, taskId);
        rpComment.deleteAllForTask(taskId);

        Optional<Comment> comment = rpComment.get(commentAdd.id());
        assertEquals(Optional.empty(), comment);
    }

    UserEntityRequest userEntityRequest = new UserEntityRequest(
            "test",
            "test"
    );

    @Test
    public void addUserTest() throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        User user = rpUser.add(userEntityRequest);

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
        User user = rpUser.add(userEntityRequest);
        Optional<User> expected = rpUser.get(user.id());

        if (expected.isEmpty()) {
            throw new NoDataException("No data found for the given ID");
        }

        assertEquals(user, expected.get());
    }

    @Test
    public void createdTasksTest() throws SQLException, NoDataException {
        User user = rpUser.add(userEntityRequest);
        assertEquals(0, user.createdTasks(paginationEntityRequest).size());
        TaskEntityRequest taskEntityRequest1 = new TaskEntityRequest(
                "test1",
                "test1",
                Status.NEW,
                Priority.medium
        );
        TaskEntityRequest taskEntityRequest2 = new TaskEntityRequest(
                "test2",
                "test2",
                Status.NEW,
                Priority.medium
        );
        List<Task> tasks = new ArrayList<>();

        tasks.add(rpTask.add(taskEntityRequest1, user.id()));
        tasks.add(rpTask.add(taskEntityRequest2, user.id()));

        assertEquals(tasks, user.createdTasks(paginationEntityRequest));

    }

    @Test
    public void assignedTasksTest() throws SQLException, NoDataException, NotAvailableException {
        User user = rpUser.add(userEntityRequest);
        User user1 = rpUser.add(userEntityRequest);
        TaskEntityRequest taskEntityRequest1 = new TaskEntityRequest(
                "test1",
                "test1",
                Status.NEW,
                Priority.medium
        );
        TaskEntityRequest taskEntityRequest2 = new TaskEntityRequest(
                "test2",
                "test2",
                Status.NEW,
                Priority.medium
        );
        TaskEntityRequest taskEntityRequest3 = new TaskEntityRequest(
                "test3",
                "test3",
                Status.NEW,
                Priority.medium
        );
        List<Task> tasks = new ArrayList<>();
        rpTask.add(taskEntityRequest3, user.id());
        tasks.add(rpTask.add(taskEntityRequest1, user.id()));
        tasks.add(rpTask.add(taskEntityRequest2, user.id()));

        rpTask.assign(tasks.get(0).id(), user1.id(), user.id());
        rpTask.assign(tasks.get(1).id(), user1.id(), user.id());

        assertEquals(tasks, user1.assignedTasks(paginationEntityRequest));
        assertEquals(0, user.assignedTasks(paginationEntityRequest).size());

    }

}
