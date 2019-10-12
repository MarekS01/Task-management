package pl.mareks.taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mareks.taskmanagement.model.TaskEntity;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity,Long> {

    Optional<TaskEntity> findById(Long id);
}
