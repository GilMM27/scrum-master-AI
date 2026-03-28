package com.springboot.MyTodoList.model;

import java.io.Serializable;
import java.util.Objects;

public class TaskAssignmentsId implements Serializable {

    private int task_id;
    private int user_id;

    public TaskAssignmentsId() {
    }

    public TaskAssignmentsId(int task_id, int user_id) {
        this.task_id = task_id;
        this.user_id = user_id;
    }

    public int getTaskId() {
        return task_id;
    }

    public void setTaskId(int task_id) {
        this.task_id = task_id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
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