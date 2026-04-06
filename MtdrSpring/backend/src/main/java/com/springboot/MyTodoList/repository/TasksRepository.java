package com.springboot.MyTodoList.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.MyTodoList.model.Sprints;
import com.springboot.MyTodoList.model.TaskStatus;
import com.springboot.MyTodoList.model.Tasks;
import java.time.OffsetDateTime;


public interface TasksRepository extends JpaRepository<Tasks, UUID> {
    
    List<Tasks> findBySprint(Sprints sprint);

    List<Tasks> findBySprintOrderByCreatedAtDesc(Sprints sprint);

    List<Tasks> findBySprintAndStatus(Sprints sprint, TaskStatus status);

    List<Tasks> findByStatus(TaskStatus status);

    List<Tasks> findByAiFlaggedTrue();

    List<Tasks> findByBlockedAtBefore(OffsetDateTime blocked_at);

    List<Tasks> findByDeliveredAtIsNull();

    Long countBySprintAndStatus(Sprints sprint, TaskStatus status);
}
