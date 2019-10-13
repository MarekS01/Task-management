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
import pl.mareks.taskmanagement.common.exception.DailyTasksNotFoundException;
import pl.mareks.taskmanagement.model.DailyTasksDTO;
import pl.mareks.taskmanagement.model.TaskEntity;
import pl.mareks.taskmanagement.service.DailyTasksService;

import java.sql.Date;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DailyTasksController.class)
public class DailyTasksControllerTest {

    @MockBean
    private DailyTasksService dailyTasksService;

    @Autowired
    private MockMvc mvc;

    private DailyTasksDTO dailyTasksDTO;
    private TaskEntity undoneTask, doneTask;

    @Before
    public void setUp() {
        undoneTask = TaskEntity.builder()
                .id(1L)
                .task("Do something")
                .isDone(false)
                .build();
        doneTask = TaskEntity.builder()
                .id(2L)
                .task("Do something else")
                .isDone(true)
                .build();
        dailyTasksDTO = DailyTasksDTO.builder()
                .id(1L)
                .date(Date.valueOf("2019-10-10"))
                .build();
    }

    @Test
    public void whenGetAllDailyTasks_thenShouldReturnsAllDailyTasks() throws Exception {
        dailyTasksDTO.setTasks(Arrays.asList(undoneTask, doneTask));
        when(dailyTasksService.findAll()).thenReturn(Arrays.asList(dailyTasksDTO));

        mvc.perform(get("/dailyTasks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{" +
                        "'id':1," +
                        "'date':'2019-10-10'," +
                        "'tasks':" +
                        "[" +
                        "{'id':1, 'task':'Do something', 'isDone':false}," +
                        "{'id':2, 'task':'Do something else', 'isDone':true}" +
                        "]" +
                        "}]"));
    }

    @Test
    public void whenGetOnlyUndoneTasks_thenShouldReturnsOnlyDailyTasksWithUndoneTasks() throws Exception {
        dailyTasksDTO.setTasks(Arrays.asList(undoneTask));
        when(dailyTasksService.findAllUncompleted()).thenReturn(Arrays.asList(dailyTasksDTO));
        mvc.perform(get("/dailyTasks/findUncompleted"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{" +
                        "'id':1," +
                        "'date':'2019-10-10'," +
                        "'tasks':" +
                        "[" +
                        "{'id':1, 'task':'Do something', 'isDone':false}" +
                        "]" +
                        "}]"));
    }

    @Test
    public void whenGetDailyTasksByExistingTasks_thenShouldReturnsDailyTasks() throws Exception {
        String searchedDay = "2019-10-10";
        when(dailyTasksService.findByDate(Date.valueOf(searchedDay))).thenReturn(Optional.of(dailyTasksDTO));

        mvc.perform(get("/dailyTasks/find")
                .param("date", searchedDay))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "'id':1," +
                        "'date':'2019-10-10'," +
                        "'tasks':null" +
                        "}"));
    }

    @Test
    public void whenGetDailyTasksByNoExistingTasks_thenShouldReturnsNotFoundStatus() throws Exception {
        String searchedDay = "2019-10-10";
        mvc.perform(get("/dailyTasks/find")
                .param("date", searchedDay))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenDeleteExistingDailyTasks_thenReturnsNoContentStatus() throws Exception {
        String searchedDay = "2019-10-10";
        mvc.perform(delete("/dailyTasks/delete")
                .param("date", searchedDay))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void whenDeleteDailyTasksByNoExistingTasks_thenShouldReturnsBadRequestStatus() throws Exception {
        String searchedDay = "2019-10-10";
        doThrow(DailyTasksNotFoundException.class)
                .when(dailyTasksService).delete(Date.valueOf(searchedDay));
        mvc.perform(delete("/dailyTasks/delete")
                .param("date", searchedDay))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}