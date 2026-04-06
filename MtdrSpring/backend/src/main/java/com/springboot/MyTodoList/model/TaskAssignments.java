package com.springboot.MyTodoList.model;

import java.util.UUID;
import com.springboot.MyTodoList.util.UUIDConverter;

import jakarta.persistence.*;

/*
    Representation of the TASK_ASSIGNMENTS table that exists already
    in the autonomous database
 */
@Entity
@Table(name = "TASK_ASSIGNMENTS")
@IdClass(TaskAssignmentsId.class)
public class TaskAssignments {
    @Id
    @Convert(converter = UUIDConverter.class)
    @Column(name = "TASK_ID", nullable = false)
    UUID task_id;
    @Id
    @Convert(converter = UUIDConverter.class)
    @Column(name = "USER_ID", nullable = false)
    UUID user_id;

    public TaskAssignments() {
    }

    public TaskAssignments(UUID task_id, UUID user_id) {
        this.task_id = task_id;
        this.user_id = user_id;
    }

    public UUID getTaskId() {
        return task_id;
    }

    public void setTaskId(UUID task_id) {
        this.task_id = task_id;
    }

    public UUID getUserId() {
        return user_id;
    }

    public void setUserId(UUID user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "TaskAssignments{" +
                "task_id=" + task_id +
                ", user_id=" + user_id +
                '}';
    }
}