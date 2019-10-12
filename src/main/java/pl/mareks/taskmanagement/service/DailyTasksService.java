package pl.mareks.taskmanagement.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mareks.taskmanagement.common.exception.DailyTasksNotFoundException;
import pl.mareks.taskmanagement.model.DailyTasksDTO;
import pl.mareks.taskmanagement.model.DailyTasksEntity;
import pl.mareks.taskmanagement.repository.DailyTasksRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Data
@RequiredArgsConstructor
public class DailyTasksService {

    private final DailyTasksRepository dailyTasksRepository;

    public List<DailyTasksDTO> findAll() {
        return dailyTasksRepository.findAll()
                .stream()
                .map(this::mapToDailyTasksDTO)
                .collect(Collectors.toList());
    }

    public List<DailyTasksDTO> findAllUncompleted() {
        return dailyTasksRepository.findAll()
                .stream()
                .peek(dailyTask -> dailyTask.setTasks(dailyTask.getTasks()
                        .stream()
                        .filter(task -> !task.getIsDone())
                        .collect(Collectors.toList())))
                .filter(task -> !task.getTasks().isEmpty())
                .map(this::mapToDailyTasksDTO)
                .collect(Collectors.toList());
    }

    public Optional<DailyTasksDTO> findByDate(Date date) {
        return dailyTasksRepository.findByDate(date).map(this::mapToDailyTasksDTO);
    }

    DailyTasksDTO addDailyTasks(Date theDate) {
        DailyTasksEntity dailyTasks = DailyTasksEntity.builder()
                .date(theDate)
                .build();
        DailyTasksEntity savedDailyTasks = dailyTasksRepository.save(dailyTasks);
        return mapToDailyTasksDTO(savedDailyTasks);
    }

    public void delete(Date date) throws DailyTasksNotFoundException {
        DailyTasksEntity theDailyTasks = dailyTasksRepository.findByDate(date)
                .orElseThrow(()->new DailyTasksNotFoundException("Tasks for day "+date+" not exist"));
        dailyTasksRepository.delete(theDailyTasks);
    }


    private DailyTasksDTO mapToDailyTasksDTO(DailyTasksEntity entity) {
        return DailyTasksDTO.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .tasks(entity.getTasks())
                .build();
    }
}
