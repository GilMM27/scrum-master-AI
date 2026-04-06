package com.springboot.MyTodoList.model;


import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import com.springboot.MyTodoList.util.UUIDConverter;

/*
    representation of the TASKS table that exists already
    in the autonomous database
 */
@Entity
@Table(name = "TASKS")
public class Tasks {
    @Id
    @Convert(converter = UUIDConverter.class)
    @Column(name = "TASK_ID", nullable = false)
    UUID task_id;
    @Column(name = "TITLE", nullable = false)
    String title;
    @Lob
    @Column(name = "DESCRIPTION")
    String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    TaskStatus status;
    @Column(name = "STORY_POINTS")
    int story_points;
    @Convert(converter = UUIDConverter.class)
    @Column(name = "SPRINT_ID", nullable = false)
    UUID sprint_id;
    @Column(name = "BLOCKED_AT")
    OffsetDateTime blocked_at;
    @Column(name = "IS_AI_FLAGGED")
    Boolean is_ai_flagged;
    @Column(name = "CREATED_AT")
    OffsetDateTime created_at;
    @Column(name = "DELIVERED_AT")
    OffsetDateTime delivered_at;

    public Tasks() {
    }

    public Tasks(UUID task_id, String title, String description, TaskStatus status, int story_points,
                 UUID sprint_id,
                 OffsetDateTime blocked_at, Boolean is_ai_flagged,
                 OffsetDateTime created_at, OffsetDateTime delivered_at) {
        this.task_id = task_id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.story_points = story_points;
        this.sprint_id = sprint_id;
        this.blocked_at = blocked_at;
        this.is_ai_flagged = is_ai_flagged;
        this.created_at = created_at;
        this.delivered_at = delivered_at;
    }

    public UUID getTaskId() {
        return task_id;
    }

    public void setTaskId(UUID task_id) {
        this.task_id = task_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getStoryPoints() {
        return story_points;
    }

    public void setStoryPoints(int story_points) {
        this.story_points = story_points;
    }

    public UUID getSprintId() {
        return sprint_id;
    }

    public void setSprintId(UUID sprint_id) {
        this.sprint_id = sprint_id;
    }

    public OffsetDateTime getBlockedAt() {
        return blocked_at;
    }

    public void setBlockedAt(OffsetDateTime blocked_at) {
        this.blocked_at = blocked_at;
    }

    public Boolean getAiFlagged() {
        return is_ai_flagged;
    }

    public void setAiFlagged(Boolean is_ai_flagged) {
        this.is_ai_flagged = is_ai_flagged;
    }

    public OffsetDateTime getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(OffsetDateTime created_at) {
        this.created_at = created_at;
    }

    public OffsetDateTime getDeliveredAt() {
        return delivered_at;
    }

    public void setDeliveredAt(OffsetDateTime delivered_at) {
        this.delivered_at = delivered_at;
    }

    @Override
    public String toString() {
        return "Tasks{" +
                "task_id=" + task_id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", story_points=" + story_points +
                ", sprint_id=" + sprint_id +
                ", blocked_at=" + blocked_at +
                ", is_ai_flagged=" + is_ai_flagged +
                ", created_at=" + created_at +
                ", delivered_at=" + delivered_at +
                '}';
    }
}
