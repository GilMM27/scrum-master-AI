package com.springboot.MyTodoList.model;


import jakarta.persistence.*;
import java.time.OffsetDateTime;

/*
    representation of the TASKS table that exists already
    in the autonomous database
 */
@Entity
@Table(name = "TASKS")
public class Tasks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, nullable = false)
    int ID;
    @Column(name = "TITLE", nullable = false)
    String title;
    @Lob
    @Column(name = "DESCRIPTION")
    String description;
    @Column(name = "STATUS")
    String status;
    @Column(name = "STORY_POINTS")
    int story_points;
    @Column(name = "SPRINT_ID", nullable = false)
    int sprint_id;
    @Column(name = "BLOCKED_AT")
    OffsetDateTime blocked_at;
    @Column(name = "IS_AI_FLAGGED")
    boolean is_ai_flagged;
    @Column(name = "CREATED_AT")
    OffsetDateTime created_at;
    @Column(name = "DELIVERED_AT")
    OffsetDateTime delivered_at;

    public Tasks() {
    }

    public Tasks(int ID, String title, String description, String status, int story_points,
                 int sprint_id,
                 OffsetDateTime blocked_at, boolean is_ai_flagged,
                 OffsetDateTime created_at, OffsetDateTime delivered_at) {
        this.ID = ID;
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

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStoryPoints() {
        return story_points;
    }

    public void setStoryPoints(int story_points) {
        this.story_points = story_points;
    }

    public int getSprintId() {
        return sprint_id;
    }

    public void setSprintId(int sprint_id) {
        this.sprint_id = sprint_id;
    }

    public OffsetDateTime getBlockedAt() {
        return blocked_at;
    }

    public void setBlockedAt(OffsetDateTime blocked_at) {
        this.blocked_at = blocked_at;
    }

    public boolean isAiFlagged() {
        return is_ai_flagged;
    }

    public void setAiFlagged(boolean is_ai_flagged) {
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
                "ID=" + ID +
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
