package com.springboot.MyTodoList.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class TaskAssignmentsId implements Serializable {

    private UUID taskId;
    private UUID userId;

    public TaskAssignmentsId() {
    }

    public TaskAssignmentsId(UUID taskId, UUID userId) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskAssignmentsId that = (TaskAssignmentsId) o;
        return Objects.hash(taskId, userId) == Objects.hash(that.taskId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, userId);
    }
}
