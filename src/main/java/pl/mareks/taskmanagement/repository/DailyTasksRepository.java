package pl.mareks.taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mareks.taskmanagement.model.DailyTasksEntity;

import java.sql.Date;
import java.util.Optional;

public interface DailyTasksRepository extends JpaRepository<DailyTasksEntity, Long> {

    Optional<DailyTasksEntity> findByDate(Date date);
}
