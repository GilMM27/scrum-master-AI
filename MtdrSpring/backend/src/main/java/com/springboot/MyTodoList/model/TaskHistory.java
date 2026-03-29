package com.springboot.MyTodoList.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/*
    Representation of the TASK_HISTORY table that exists already
    in the autonomous database
 */
@Entity
@Table(name = "TASK_HISTORY")
public class TaskHistory {
    @Id
    @Column(name = "HISTORY_ID", nullable = false)
    int history_id;
    @Column(name = "TASK_ID")
    int task_id;
    @Column(name = "OLD_STATUS")
    String old_status;
    @Column(name = "NEW_STATUS")
    String new_status;
    @Column(name = "CHANGED_AT")
    OffsetDateTime changed_at;

    public TaskHistory() {
    }

    public TaskHistory(int history_id, int task_id, String old_status, String new_status, OffsetDateTime changed_at) {
        this.history_id = history_id;
        this.task_id = task_id;
        this.old_status = old_status;
        this.new_status = new_status;
        this.changed_at = changed_at;
    }

    public int getHistoryId() {
        return history_id;
    }

    public void setHistoryId(int history_id) {
        this.history_id = history_id;
    }

    public int getTaskId() {
        return task_id;
    }

    public void setTaskId(int task_id) {
        this.task_id = task_id;
    }

    public String getOldStatus() {
        return old_status;
    }

    public void setOldStatus(String old_status) {
        this.old_status = old_status;
    }

    public String getNewStatus() {
        return new_status;
    }

    public void setNewStatus(String new_status) {
        this.new_status = new_status;
    }

    public OffsetDateTime getChangedAt() {
        return changed_at;
    }

    public void setChangedAt(OffsetDateTime changed_at) {
        this.changed_at = changed_at;
    }

    @Override
    public String toString() {
        return "TaskHistory{" +
                "history_id=" + history_id +
                ", task_id=" + task_id +
                ", old_status='" + old_status + '\'' +
                ", new_status='" + new_status + '\'' +
                ", changed_at=" + changed_at +
                '}';
    }
}