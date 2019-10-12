package pl.mareks.taskmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mareks.taskmanagement.common.exception.TaskNotFoundException;
import pl.mareks.taskmanagement.model.DailyTasksDTO;
import pl.mareks.taskmanagement.model.DailyTasksEntity;
import pl.mareks.taskmanagement.model.TaskDTO;
import pl.mareks.taskmanagement.model.TaskEntity;
import pl.mareks.taskmanagement.repository.TaskRepository;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final DailyTasksService dailyTasksService;

    public List<TaskDTO> findAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::mapToTaskDTO)
                .collect(Collectors.toList());
    }

    public TaskDTO addTask(Date taskForDay, TaskDTO taskDTO) {
        TaskEntity taskEntity = mapToTaskEntity(taskDTO);
        DailyTasksDTO theDailyTasks = dailyTasksService
                .findByDate(taskForDay)
                .orElseGet(() -> dailyTasksService.addDailyTasks(taskForDay));
        taskEntity.setDailyTasks(mapToDailyTasksEntity(theDailyTasks));
        TaskEntity savedTask = taskRepository.save(taskEntity);
        return mapToTaskDTO(savedTask);
    }

    public TaskDTO doneTask(Long taskId) throws TaskNotFoundException {
        TaskEntity theTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + taskId + " not exist"));
        theTask.setIsDone(true);
        TaskEntity updatedTask = taskRepository.save(theTask);
        return mapToTaskDTO(updatedTask);
    }

    public void deleteTask(Long taskId) throws TaskNotFoundException {
        TaskEntity theTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + taskId + " not exist"));
        taskRepository.delete(theTask);
    }

    private TaskEntity mapToTaskEntity(TaskDTO dto) {
        return TaskEntity.builder()
                .task(dto.getTask())
                .isDone(dto.getIsDone())
                .build();
    }

    private TaskDTO mapToTaskDTO(TaskEntity entity) {
        return TaskDTO.builder()
                .id(entity.getId())
                .task(entity.getTask())
                .isDone(entity.getIsDone())
                .build();
    }

    private DailyTasksEntity mapToDailyTasksEntity(DailyTasksDTO dto) {
        return DailyTasksEntity.builder()
                .id(dto.getId())
                .date(dto.getDate())
                .tasks(dto.getTasks())
                .build();
    }
}
