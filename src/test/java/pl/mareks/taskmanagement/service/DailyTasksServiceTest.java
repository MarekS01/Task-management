package pl.mareks.taskmanagement.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import pl.mareks.taskmanagement.common.exception.DailyTasksNotFoundException;
import pl.mareks.taskmanagement.model.DailyTasksDTO;
import pl.mareks.taskmanagement.model.DailyTasksEntity;
import pl.mareks.taskmanagement.model.TaskEntity;
import pl.mareks.taskmanagement.repository.DailyTasksRepository;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DailyTasksServiceTest {

    @MockBean
    private DailyTasksRepository dailyTasksRepository;

    @Autowired
    private DailyTasksService dailyTasksService;

    private DailyTasksEntity dailyTasksEntity;
    private DailyTasksDTO dailyTasksDTO;
    private TaskEntity undoneTask, doneTask;

    @Captor
    private ArgumentCaptor<DailyTasksEntity> argumentCaptor;

    @Before
    public void setUp() {
        undoneTask = TaskEntity.builder()
                .task("Do something")
                .isDone(false)
                .build();

        doneTask = TaskEntity.builder()
                .task("Do something else")
                .isDone(true)
                .build();

        dailyTasksEntity = DailyTasksEntity.builder()
                .date(Date.valueOf("2019-10-10"))
                .tasks(Arrays.asList(undoneTask, doneTask))
                .build();

        dailyTasksDTO = DailyTasksDTO.builder()
                .date(dailyTasksEntity.getDate())
                .tasks(dailyTasksEntity.getTasks())
                .build();
    }

    @Test
    public void whenFindAllDailyTasks_thenShouldReturnsAllDailyTasks() {
        when(dailyTasksRepository.findAll())
                .thenReturn(Arrays.asList(dailyTasksEntity));

        List<DailyTasksDTO> foundTasks = dailyTasksService.findAll();

        assertEquals(1, foundTasks.size());
        assertEquals(2, foundTasks.get(0).getTasks().size());
    }

    @Test
    public void whenFindDailyTaskByDate_thenShouldReturnsFoundDailyTasks() {
        Date existingDailyTasksDate = Date.valueOf("2019-10-10");
        when(dailyTasksRepository.findByDate(existingDailyTasksDate))
                .thenReturn(Optional.of(dailyTasksEntity));

        Optional<DailyTasksDTO> foundDailyTasks = dailyTasksService.findByDate(existingDailyTasksDate);

        assertTrue(foundDailyTasks.isPresent());
        assertEquals(dailyTasksDTO, foundDailyTasks.get());
    }

    @Test
    public void whenFindOnlyUndoneDailyTasks_thenShouldReturnsOnlyDailyTasksWithUndoneTasks() {
        DailyTasksEntity onlyDoneDailyTask = DailyTasksEntity.builder()
                .date(Date.valueOf("2019-10-12"))
                .tasks(Arrays.asList(doneTask))
                .build();
        when(dailyTasksRepository.findAll()).thenReturn(Arrays.asList(dailyTasksEntity, onlyDoneDailyTask));

        List<DailyTasksDTO> foundAllUndoneTasks = dailyTasksService.findAllUncompleted();

        assertEquals(1, foundAllUndoneTasks.size());
        assertEquals(1, foundAllUndoneTasks.get(0).getTasks().size());
    }

    @Test
    public void whenAddDailyTasks_thenShouldSavedDailyTasks() {
        Date dailyTasksDate = Date.valueOf("2019-10-10");
        when(dailyTasksRepository.save(any())).thenReturn(dailyTasksEntity);

        DailyTasksDTO savedDailyTasks = dailyTasksService.addDailyTasks(dailyTasksDate);

        assertEquals(dailyTasksDTO, savedDailyTasks);
    }

    @Test
    public void whenDeleteExistingDailyTasks_thenShouldRemoveDailyTasks() throws DailyTasksNotFoundException {
        Date dailyTasksDate = Date.valueOf("2019-10-10");
        when(dailyTasksRepository.findByDate(dailyTasksDate)).thenReturn(Optional.of(dailyTasksEntity));
        dailyTasksService.delete(dailyTasksDate);

        verify(dailyTasksRepository, times(1)).delete(argumentCaptor.capture());
        assertEquals(dailyTasksEntity, argumentCaptor.getValue());
    }

    @Test(expected = DailyTasksNotFoundException.class)
    public void whenDeleteNoExistingDailyTasks_thenShouldReturnsDailyTasksNotFoundException() throws DailyTasksNotFoundException {
        Date dailyTasksDate = Date.valueOf("2019-10-10");
        dailyTasksService.delete(dailyTasksDate);
    }
}