package com.springboot.MyTodoList.model;

import com.springboot.MyTodoList.util.UUIDConverter;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/*
    Representation of the TASK_HISTORY table that exists already
    in the autonomous database
*/
@Entity
@Table(name = "TASK_HISTORY")
public class TaskHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "HISTORY_ID")
    UUID historyId;
    @Convert(converter = UUIDConverter.class)
    @Column(name = "TASK_ID")
    UUID taskId;
    @Enumerated(EnumType.STRING)
    @Column(name = "OLD_STATUS")
    TaskStatus oldStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "NEW_STATUS")
    TaskStatus newStatus;
    @Column(name = "CHANGED_AT")
    OffsetDateTime changedAt;

    public TaskHistory() {
    }

    public TaskHistory(UUID historyId, UUID taskId, TaskStatus oldStatus, TaskStatus newStatus, OffsetDateTime changedAt) {
        this.historyId = historyId;
        this.taskId = taskId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
    }

    public UUID getHistoryId() {
        return historyId;
    }

    public void setHistoryId(UUID historyId) {
        this.historyId = historyId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public TaskStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(TaskStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public TaskStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(TaskStatus newStatus) {
        this.newStatus = newStatus;
    }

    public OffsetDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(OffsetDateTime changedAt) {
        this.changedAt = changedAt;
    }

    @Override
    public String toString() {
        return "TaskHistory{" +
                "historyId=" + historyId +
                ", taskId=" + taskId +
                ", oldStatus='" + oldStatus + '\'' +
                ", newStatus='" + newStatus + '\'' +
                ", changedAt=" + changedAt +
                '}';
    }
}
