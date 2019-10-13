package pl.mareks.taskmanagement.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.mareks.taskmanagement.model.DailyTasksDTO;
import pl.mareks.taskmanagement.model.TaskDTO;
import pl.mareks.taskmanagement.service.DailyTasksService;
import pl.mareks.taskmanagement.service.TaskService;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureMockMvc
public class TaskTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private DailyTasksService dailyTasksService;

    @Autowired
    private MockMvc mvc;

    private TaskDTO taskDTO;

    @Before
    public void setUp() {
        taskDTO = TaskDTO.builder()
                .task("Do something")
                .isDone(false)
                .build();
    }

    @Test
    public void whenDoAllOperationWithTask_thenShouldDoAllOperations() throws Exception {
        String taskForDay = "2019-10-10";
        String addTaskInJson = "{\"task\":\"Do something\",\"isDone\":false}";
        mvc.perform(post("/task")
                .param("date", taskForDay)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addTaskInJson))
                .andDo(print())
                .andExpect(status().isCreated());
        List<DailyTasksDTO> allDailyTasks = dailyTasksService.findAll();
        assertEquals(1, allDailyTasks.size());
        assertEquals(Date.valueOf(taskForDay), allDailyTasks.get(0).getDate());

        mvc.perform(get("/task"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"task\":\"Do something\",\"isDone\":false}]"));

        mvc.perform(put("/task/done")
                .param("id", "1"))
                .andDo(print())
                .andExpect(status().isCreated());

        List<TaskDTO> allTasks = taskService.findAllTasks();
        assertEquals(1, allTasks.size());
        assertTrue(allTasks.get(0).getIsDone());

        mvc.perform(delete("/task/delete")
                .param("id", "1"))
                .andDo(print())
                .andExpect(status().isNoContent());
        List<TaskDTO> allTaskAfterDeleting = taskService.findAllTasks();
        assertEquals(0, allTaskAfterDeleting.size());
    }

}
