package com.springboot.MyTodoList.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.MyTodoList.model.Projects;
import com.springboot.MyTodoList.model.SprintStatus;
import com.springboot.MyTodoList.model.Sprints;

public interface SprintsRepository extends JpaRepository<Sprints, UUID> {
    
    List<Sprints> findByProject(Projects project);

    List<Sprints> findByProjectOrderByStartDateDesc(Projects project);

    Optional<Sprints> findByProjectAndStatus(Projects project, SprintStatus status);

    List<Sprints> findByStatus(SprintStatus status);

    List<Sprints> findByStartDateValidRange(LocalDate start_date, LocalDate end_date);
}
