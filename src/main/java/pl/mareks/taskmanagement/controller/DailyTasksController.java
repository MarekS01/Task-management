package pl.mareks.taskmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.mareks.taskmanagement.model.DailyTasksDTO;
import pl.mareks.taskmanagement.service.DailyTasksService;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dailyTasks")
@RequiredArgsConstructor
public class DailyTasksController {

    private final DailyTasksService dailyTasksService;

    @GetMapping
    public ResponseEntity<List<DailyTasksDTO>> getAll() {
        return ResponseEntity.ok(dailyTasksService.findAll());
    }

    @GetMapping("findUncompleted")
    public ResponseEntity<List<DailyTasksDTO>> getOnlyUncompletedTasks(){
        return ResponseEntity.ok(dailyTasksService.findAllUncompleted());
    }

    @GetMapping("/find")
    public ResponseEntity<DailyTasksDTO> getByDate(@RequestParam("date") Date theDate){
        Optional<DailyTasksDTO> taskForDay = dailyTasksService.findByDate(theDate);
        return ResponseEntity.of(taskForDay);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam("date") Date theDate) {
        dailyTasksService.delete(theDate);
        return ResponseEntity.status(204).build();
    }
}
