package pl.mareks.taskmanagement.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import pl.mareks.taskmanagement.model.TaskEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private TaskEntity task;

    @Before
    public void setUp() {
        task = TaskEntity.builder()
                .task("Do something")
                .isDone(false)
                .build();
    }

    @Test
    public void whenFindByIdSavedTask_thenShouldReturnsFoundTask() {
        Long savedTaskId = (Long) testEntityManager.persistAndGetId(task);

        Optional<TaskEntity> foundTask = taskRepository.findById(savedTaskId);

        assertTrue(foundTask.isPresent());
        assertEquals("Do something", foundTask.get().getTask());
        assertFalse(foundTask.get().getIsDone());
    }

    @Test
    public void whenFindByNoExistingIdTask_ThenShouldReturnsEmptyOptional() {
        Optional<TaskEntity> foundTask = taskRepository.findById(1L);
        assertTrue(foundTask.isEmpty());
    }

    @Test
    public void whenFindAllTasks_thenShouldReturnsListOfTasks() {
        TaskEntity task2 = TaskEntity.builder()
                .task("Do something else")
                .isDone(false)
                .build();
        testEntityManager.persistAndFlush(task);
        testEntityManager.persistAndFlush(task2);

        List<TaskEntity> allTasks = taskRepository.findAll();

        assertEquals(2, allTasks.size());
    }

    @Test
    public void whenDeleteExistingTask_thenShouldRemoveTask() {
        TaskEntity savedTask = testEntityManager.persistAndFlush(task);
        Optional<TaskEntity> foundTask = taskRepository.findById(savedTask.getId());
        assertTrue(foundTask.isPresent());

        taskRepository.delete(savedTask);
        Optional<TaskEntity> foundAfterDeletedTask = taskRepository.findById(savedTask.getId());
        assertTrue(foundAfterDeletedTask.isEmpty());
    }
}