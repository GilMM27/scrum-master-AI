package com.springboot.MyTodoList.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.MyTodoList.model.Tasks;
import com.springboot.MyTodoList.model.TaskStatus;
import java.time.OffsetDateTime;


public interface TasksRepository extends JpaRepository<Tasks, UUID> {
    
    List<Tasks> findBySprintId(UUID sprintId);

    List<Tasks> findBySprintIdOrderByCreatedAtDesc(UUID sprintId);

    List<Tasks> findBySprintIdAndStatus(UUID sprintId, TaskStatus status);

    List<Tasks> findByStatus(TaskStatus status);

    List<Tasks> findByIsAiFlaggedTrue();

    List<Tasks> findByBlockedAtBefore(OffsetDateTime blockedAt);

    List<Tasks> findByDeliveredAtIsNull();

    Long countBySprintIdAndStatus(UUID sprintId, TaskStatus status);
}
