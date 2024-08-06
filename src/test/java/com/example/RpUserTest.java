package com.example;

import com.example.db.InitializedTestContainer;
import com.example.data.entity.task.RpTask;
import com.example.data.entity.user.RpUser;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

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
