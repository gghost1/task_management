package com.example;

import com.example.db.InitializedTestContainer;
import com.example.dto.TaskDto;
import com.example.dto.UserDto;
import com.example.entity.comment.RpComment;
import com.example.entity.elements.Priority;
import com.example.entity.elements.Status;
import com.example.entity.task.RpTask;
import com.example.entity.task.Task;
import com.example.entity.user.RpUser;
import com.example.entity.user.User;
import com.example.entity.user.UserEntity;
import com.example.exceptions.NoDataException;
import com.example.exceptions.NotAvailableException;
import com.jcabi.jdbc.JdbcSession;
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
public class RpUserTest extends InitializedTestContainer {
    @Autowired
    private DataSource dataSource;

    private RpUser rpUser;
    private RpTask rpTask;

    @BeforeEach
    public void init() {
        rpUser = new RpUser(dataSource);
        rpTask = new RpTask(dataSource);
    }

//    UserDto userDto = new UserDto(
//            null,
//            "test",
//            "test"
//    );
//
//    @Test
//    public void addUserTest() throws SQLException {
//        JdbcSession jdbcSession = new JdbcSession(dataSource);
//        User user = rpUser.add(userDto);
//
//        User expected = jdbcSession.sql("""
//                            SELECT id, email, password from users
//                            WHERE id = ?
//                            """)
//                .set(user.id())
//                .select((rset, stmt) -> {
//                    if (rset.next()) {
//                        return new UserEntity(
//                                UUID.fromString(rset.getString("id")),
//                                rset.getString("email"),
//                                rset.getString("password"),
//                                rpUser.dataSource
//                        );
//                    } else {
//                        throw new SQLException("No data found for the given ID");
//                    }
//                });
//        assertEquals(expected, user);
//    }
//
//    @Test
//    public void getUserTest() throws SQLException, NoDataException {
//        User user = rpUser.add(userDto);
//        Optional<User> expected = rpUser.get(user.id());
//
//        if (expected.isEmpty()) {
//            throw new NoDataException("No data found for the given ID");
//        }
//
//        assertEquals(user, expected.get());
//    }
//
//    @Test
//    public void createdTasksTest() throws SQLException, NoDataException {
//        User user = rpUser.add(userDto);
//        assertEquals(0, user.createdTasks().size());
//        TaskDto taskDto1 = new TaskDto(
//                null,
//                "test1",
//                "test1",
//                Status.NEW,
//                Priority.medium,
//                user.id()
//        );
//        TaskDto taskDto2 = new TaskDto(
//                null,
//                "test2",
//                "test2",
//                Status.NEW,
//                Priority.medium,
//                user.id()
//        );
//        List<Task> tasks = new ArrayList<>();
//
//        tasks.add(rpTask.add(taskDto1));
//        tasks.add(rpTask.add(taskDto2));
//
//        assertEquals(tasks, user.createdTasks());
//
//    }
//
//    @Test
//    public void assignedTasksTest() throws SQLException, NoDataException, NotAvailableException {
//        User user = rpUser.add(userDto);
//        User user1 = rpUser.add(userDto);
//        TaskDto taskDto1 = new TaskDto(
//                null,
//                "test1",
//                "test1",
//                Status.NEW,
//                Priority.medium,
//                user.id()
//        );
//        TaskDto taskDto2 = new TaskDto(
//                null,
//                "test2",
//                "test2",
//                Status.NEW,
//                Priority.medium,
//                user.id()
//        );
//        TaskDto taskDto3 = new TaskDto(
//                null,
//                "test3",
//                "test3",
//                Status.NEW,
//                Priority.medium,
//                user.id()
//        );
//        List<Task> tasks = new ArrayList<>();
//        rpTask.add(taskDto3);
//        tasks.add(rpTask.add(taskDto1));
//        tasks.add(rpTask.add(taskDto2));
//
//        rpTask.assign(tasks.get(0).id(), user1.id(), user.id());
//        rpTask.assign(tasks.get(1).id(), user1.id(), user.id());
//
//        assertEquals(tasks, user1.assignedTasks());
//        assertEquals(0, user.assignedTasks().size());
//
//    }
}
