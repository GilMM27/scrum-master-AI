package com.springboot.MyTodoList.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class TaskAssignmentsId implements Serializable {

    private UUID task_id;
    private UUID user_id;

    public TaskAssignmentsId() {
    }

    public TaskAssignmentsId(UUID task_id, UUID user_id) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskAssignmentsId that = (TaskAssignmentsId) o;
        return task_id == that.task_id && user_id == that.user_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(task_id, user_id);
    }
}