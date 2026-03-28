package com.springboot.MyTodoList.model;

import jakarta.persistence.*;

/*
    Representation of the TASK_HISTORY table that exists already
    in the autonomous database
 */
@Entity
@Table(name = "TASK_ASSIGNMENTS")
@IdClass(TaskAssignmentsId.class)
public class TaskAssignments {
    @Id
    @Column(name = "TASK_ID", nullable = false)
    int task_id;
    @Id
    @Column(name = "USER_ID", nullable = false)
    int user_id;

    public TaskAssignments() {
    }

    public TaskAssignments(int task_id, int user_id) {
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
    public String toString() {
        return "TaskAssignments{" +
                "task_id=" + task_id +
                ", user_id=" + user_id +
                '}';
    }
}