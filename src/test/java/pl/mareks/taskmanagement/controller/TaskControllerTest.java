package pl.mareks.taskmanagement.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.mareks.taskmanagement.common.exception.TaskNotFoundException;
import pl.mareks.taskmanagement.model.TaskDTO;
import pl.mareks.taskmanagement.service.TaskService;

import java.sql.Date;
import java.util.Arrays;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @MockBean
    private TaskService taskService;

    @Autowired
    private MockMvc mvc;

    private TaskDTO task;

    @Before
    public void setUp() {
        task = TaskDTO.builder()
                .task("Do something")
                .isDone(false)
                .build();
    }

    @Test
    public void whenFindAllTasks_thenShouldReturnsAllTasks() throws Exception {
        task.setId(1L);
        when(taskService.findAllTasks()).thenReturn(Arrays.asList(task));

        mvc.perform(get("/task"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{" +
                        "'id':1," +
                        "'task':'Do something'," +
                        "'isDone':false" +
                        "}]"));
    }

    @Test
    public void whenCreateNewTask_thenShouldAddNewTask() throws Exception {
        String date = "2019-10-10";
        Date taskForDay = Date.valueOf(date);
        String addTaskInJson = "{\"task\":\"Do something\",\"isDone\":false}";
        when(taskService.addTask(taskForDay, task)).thenReturn(task);

        mvc.perform(post("/task")
                .param("date", date)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addTaskInJson))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void whenDoneExistingTask_thenShouldCompleteTheTask() throws Exception {
        mvc.perform(put("/task/done")
                .param("id", "1"))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void whenDoneNotExistingTask_thenShouldReturnsTaskNotFoundException() throws Exception {
        doThrow(TaskNotFoundException.class)
                .when(taskService).doneTask(1L);

        mvc.perform(put("/task/done")
                .param("id", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenDeleteExistingTask_thenShouldDeleteTask() throws Exception {
        mvc.perform(delete("/task/delete")
                .param("id", "1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void whenDeleteNotExistingTask_thenShouldReturnsTaskNotFoundException() throws Exception {
        doThrow(TaskNotFoundException.class)
                .when(taskService).deleteTask(1L);
        mvc.perform(delete("/task/delete")
                .param("id", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}