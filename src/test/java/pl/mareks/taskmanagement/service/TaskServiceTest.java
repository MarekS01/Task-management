package pl.mareks.taskmanagement.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import pl.mareks.taskmanagement.common.exception.TaskNotFoundException;
import pl.mareks.taskmanagement.model.DailyTasksDTO;
import pl.mareks.taskmanagement.model.TaskDTO;
import pl.mareks.taskmanagement.model.TaskEntity;
import pl.mareks.taskmanagement.repository.TaskRepository;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskServiceTest {

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private DailyTasksService dailyTasksService;

    @Autowired
    private TaskService taskService;

    private TaskEntity taskEntity;
    private TaskDTO taskDTO;

    @Before
    public void setUp() {
        taskEntity = TaskEntity.builder()
                .id(1L)
                .task("Done something")
                .isDone(false)
                .build();

        taskDTO = TaskDTO.builder()
                .id(1L)
                .task(taskEntity.getTask())
                .isDone(taskEntity.getIsDone())
                .build();
    }

    @Test
    public void whenFindAllTasks_thenShouldReturnsAllTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(taskEntity));

        List<TaskDTO> allFoundTasks = taskService.findAllTasks();

        assertEquals(1, allFoundTasks.size());
    }

    @Test
    public void whenAddTaskForNoExistingDay_thenShouldCreateDailyTasksAndSavedTask() {
        Date taskForDay = Date.valueOf("2019-10-10");
        DailyTasksDTO dailyTasksDTO = DailyTasksDTO.builder()
                .date(taskForDay)
                .build();
        when(dailyTasksService.addDailyTasks(taskForDay)).thenReturn(dailyTasksDTO);
        when(taskRepository.save(any())).thenReturn(taskEntity);

        TaskDTO savedTask = taskService.addTask(taskForDay, this.taskDTO);

        assertEquals(taskDTO, savedTask);
        verify(dailyTasksService, times(1)).addDailyTasks(taskForDay);
    }

    @Test
    public void whenAddTaskForExistingDay_thenShouldSaveTaskWithoutAddingNewDailyTasks() {
        Date taskForDay = Date.valueOf("2019-10-10");
        DailyTasksDTO dailyTasksDTO = DailyTasksDTO.builder()
                .id(1L)
                .date(taskForDay)
                .build();
        when(dailyTasksService.findByDate(taskForDay)).thenReturn(Optional.of(dailyTasksDTO));
        when(taskRepository.save(any())).thenReturn(taskEntity);

        TaskDTO savedTask = taskService.addTask(taskForDay, this.taskDTO);

        assertEquals(taskDTO, savedTask);
        verify(dailyTasksService, times(0)).addDailyTasks(taskForDay);
    }

    @Test
    public void whenDoneExistingTask_thenTaskIsDone() throws TaskNotFoundException {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        taskEntity.setIsDone(true);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        TaskDTO doneTask = taskService.doneTask(1L);

        assertEquals(taskEntity.getTask(), doneTask.getTask());
        assertTrue(doneTask.getIsDone());
    }

    @Test(expected = TaskNotFoundException.class)
    public void whenDoneNotExistingTask_thenShouldReturnsTaskNotFoundException() throws TaskNotFoundException {
        taskService.doneTask(1L);
    }

    @Test
    public void whenDeleteExistingTask_thenShouldRemoveTask() throws TaskNotFoundException {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(taskEntity);
    }

    @Test(expected = TaskNotFoundException.class)
    public void whenDeleteNotExistingTask_thenShouldReturnsTaskNotFoundException() throws TaskNotFoundException {
        taskService.deleteTask(1L);
    }
}