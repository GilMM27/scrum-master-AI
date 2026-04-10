package com.springboot.MyTodoList.dto;

import com.springboot.MyTodoList.model.TaskStatus;
import com.springboot.MyTodoList.model.Tasks;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CreateTaskRequest {
    private String title;
    private String description;
    private UUID sprintId;
    private Integer storyPoints;
    private UUID assigneeId;

    public Tasks toEntity() {
        Tasks task = new Tasks();
        task.setTitle(this.title);
        task.setDescription(this.description);
        task.setSprintId(this.sprintId);
        task.setStoryPoints(this.storyPoints != null ? this.storyPoints : 0);
        task.setStatus(TaskStatus.TO_DO);
        task.setCreatedAt(OffsetDateTime.now());
        task.setAiFlagged(false);
        task.setBlockedAt(null);
        task.setDeliveredAt(null);
        return task;
    }

    public CreateTaskRequest() {
    }

    public CreateTaskRequest(String title, String description, UUID sprintId, Integer storyPoints, UUID assigneeId) {
        this.title = title;
        this.description = description;
        this.sprintId = sprintId;
        this.storyPoints = storyPoints;
        this.assigneeId = assigneeId;
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

    public UUID getSprintId() {
        return sprintId;
    }

    public void setSprintId(UUID sprintId) {
        this.sprintId = sprintId;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public UUID getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(UUID assigneeId) {
        this.assigneeId = assigneeId;
    }
}
