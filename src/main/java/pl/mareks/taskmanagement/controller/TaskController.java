package pl.mareks.taskmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.mareks.taskmanagement.common.exception.TaskNotFoundException;
import pl.mareks.taskmanagement.model.TaskDTO;
import pl.mareks.taskmanagement.service.TaskService;

import javax.validation.Valid;
import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDTO>> findAll() {
        return ResponseEntity.ok(taskService.findAllTasks());
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestParam("date") Date theDate,
                                       @RequestBody @Valid TaskDTO taskDTO,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(400).build();
        }
        taskService.addTask(theDate, taskDTO);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/done")
    public ResponseEntity<Void> doneTask(@RequestParam("id") Long id) {
        try {
            taskService.doneTask(id);
        } catch (TaskNotFoundException e) {
            e.getMessage();
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteTask(@RequestParam("id") Long id) {
        try {
            taskService.deleteTask(id);
        } catch (TaskNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.status(204).build();
    }
}
