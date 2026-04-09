package com.springboot.MyTodoList.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.MyTodoList.model.TaskAssignments;
import com.springboot.MyTodoList.model.TaskAssignmentsId;

public interface TaskAssignmentsRepository extends JpaRepository<TaskAssignments, TaskAssignmentsId> {
    
    List<TaskAssignments> findByUserId(UUID userId);

    List<TaskAssignments> findByTaskId(UUID taskId);

    Boolean existsByTaskIdAndUserId(UUID taskId, UUID userId);

    void deleteByTaskId(UUID taskId);

    void deleteByUserId(UUID userId);
}
