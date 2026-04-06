package com.springboot.MyTodoList.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.MyTodoList.model.TaskHistory;
import com.springboot.MyTodoList.model.Tasks;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, UUID> {
    
    List<TaskHistory> findByTaskOrderByChangedAtDesc(Tasks task);
}
