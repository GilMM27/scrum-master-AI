package com.springboot.MyTodoList.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.MyTodoList.model.TaskAssignments;
import com.springboot.MyTodoList.model.TaskAssignmentsId;
import com.springboot.MyTodoList.model.Tasks;
import com.springboot.MyTodoList.model.Users;

public interface TaskAssignmentsRepository extends JpaRepository<TaskAssignments, TaskAssignmentsId> {
    
    List<TaskAssignments> findByUser(Users user);

    List<TaskAssignments> findByTask(Tasks task);

    Boolean existsByTaskAndUser(Tasks task, Users user);

    void deleteByTask(Tasks task);

    void deleteByUser(Users user);
}
