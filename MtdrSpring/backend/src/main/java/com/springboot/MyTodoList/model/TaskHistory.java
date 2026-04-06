package com.springboot.MyTodoList.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import com.springboot.MyTodoList.util.UUIDConverter;

/*
    Representation of the TASK_HISTORY table that exists already
    in the autonomous database
 */
@Entity
@Table(name = "TASK_HISTORY")
public class TaskHistory {
    @Id
    @Convert(converter = UUIDConverter.class)
    @Column(name = "HISTORY_ID", nullable = false)
    UUID history_id;
    @Convert(converter = UUIDConverter.class)
    @Column(name = "TASK_ID")
    UUID task_id;
    @Enumerated(EnumType.STRING)
    @Column(name = "OLD_STATUS")
    TaskStatus old_status;
    @Enumerated(EnumType.STRING)
    @Column(name = "NEW_STATUS")
    TaskStatus new_status;
    @Column(name = "CHANGED_AT")
    OffsetDateTime changed_at;

    public TaskHistory() {
    }

    public TaskHistory(UUID history_id, UUID task_id, TaskStatus old_status, TaskStatus new_status, OffsetDateTime changed_at) {
        this.history_id = history_id;
        this.task_id = task_id;
        this.old_status = old_status;
        this.new_status = new_status;
        this.changed_at = changed_at;
    }

    public UUID getHistoryId() {
        return history_id;
    }

    public void setHistoryId(UUID history_id) {
        this.history_id = history_id;
    }

    public UUID getTaskId() {
        return task_id;
    }

    public void setTaskId(UUID task_id) {
        this.task_id = task_id;
    }

    public TaskStatus getOldStatus() {
        return old_status;
    }

    public void setOldStatus(TaskStatus old_status) {
        this.old_status = old_status;
    }

    public TaskStatus getNewStatus() {
        return new_status;
    }

    public void setNewStatus(TaskStatus new_status) {
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