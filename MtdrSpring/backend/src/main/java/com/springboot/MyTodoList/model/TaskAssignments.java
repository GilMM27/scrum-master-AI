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
    UUID taskId;
    @Id
    @Convert(converter = UUIDConverter.class)
    @Column(name = "USER_ID", nullable = false)
    UUID userId;

    public TaskAssignments() {
    }

    public TaskAssignments(UUID taskId, UUID userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "TaskAssignments{" +
                "taskId=" + taskId +
                ", userId=" + userId +
                '}';
    }
}
