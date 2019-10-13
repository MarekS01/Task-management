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
import pl.mareks.taskmanagement.model.DailyTasksEntity;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class DailyTasksRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private DailyTasksRepository dailyTasksRepository;

    private DailyTasksEntity dailyTasksEntity;

    @Before
    public void setUp() {
        dailyTasksEntity = DailyTasksEntity.builder()
                .date(Date.valueOf("2019-10-10"))
                .build();
    }

    @Test
    public void whenFindAllDailyTasks_thenShouldReturnsListOfDailyTasks() {
        DailyTasksEntity dailyTasksEntity2 = DailyTasksEntity.builder()
                .date(Date.valueOf("2019-10-11"))
                .build();

        testEntityManager.persistAndFlush(dailyTasksEntity);
        testEntityManager.persistAndFlush(dailyTasksEntity2);

        List<DailyTasksEntity> foundDailyTasks = dailyTasksRepository.findAll();

        assertEquals(2, foundDailyTasks.size());
    }

    @Test
    public void whenFindExistingDailyTasksByDate_thenShouldReturnsFoundDailyTasks() {
        testEntityManager.persistAndFlush(dailyTasksEntity);

        Optional<DailyTasksEntity> foundDailyTasks = dailyTasksRepository.findByDate(Date.valueOf("2019-10-10"));

        assertTrue(foundDailyTasks.isPresent());
        assertEquals(Date.valueOf("2019-10-10"), foundDailyTasks.get().getDate());
    }

    @Test
    public void whenFindNoExistingDailyTasksByDate_thenShouldReturnsEmptyOptional() {
        Optional<DailyTasksEntity> foundDailyTasks = dailyTasksRepository.findByDate(Date.valueOf("2019-10-12"));

        assertTrue(foundDailyTasks.isEmpty());
    }

    @Test
    public void whenDeleteDailyTasks_thenShouldRemoveDailyTasks() {
        DailyTasksEntity savedDailyTasks = testEntityManager.persistAndFlush(this.dailyTasksEntity);
        Optional<DailyTasksEntity> foundBeforeDeleting = dailyTasksRepository.findByDate(Date.valueOf("2019-10-10"));
        assertTrue(foundBeforeDeleting.isPresent());

        dailyTasksRepository.delete(savedDailyTasks);
        Optional<DailyTasksEntity> foundAfterDeleting = dailyTasksRepository.findByDate(Date.valueOf("2019-10-10"));
        assertTrue(foundAfterDeleting.isEmpty());
    }
}