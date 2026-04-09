package com.springboot.MyTodoList.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.MyTodoList.model.Sprints;
import com.springboot.MyTodoList.model.SprintStatus;

public interface SprintsRepository extends JpaRepository<Sprints, UUID> {
    
    List<Sprints> findByProjectId(UUID projectId);

    List<Sprints> findByProjectIdOrderByStartDateDesc(UUID projectId);

    Optional<Sprints> findByProjectIdAndStatus(UUID projectId, SprintStatus status);

    List<Sprints> findByStatus(SprintStatus status);

    List<Sprints> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
}
