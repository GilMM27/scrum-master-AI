package com.springboot.MyTodoList.dto;

import com.springboot.MyTodoList.model.TaskPriority;
import com.springboot.MyTodoList.model.TaskStatus;
import com.springboot.MyTodoList.model.Tasks;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class CreateTaskRequest {
    private UUID projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private UUID sprintId;
    private Integer storyPoints;
    private Integer expectedHours;
    private List<UUID> assigneeIds;

    public CreateTaskRequest() {
    }

    public Tasks toEntity() {
        Tasks task = new Tasks();
        task.setProjectId(this.projectId);
        task.setTitle(this.title);
        task.setDescription(this.description);
        task.setSprintId(this.sprintId);
        task.setStoryPoints(this.storyPoints != null ? this.storyPoints : 0);
        task.setExpectedHours(this.expectedHours != null ? this.expectedHours : 0);
        task.setStatus(this.status != null ? this.status : TaskStatus.TO_DO);
        task.setPriority(this.priority != null ? this.priority : TaskPriority.MEDIUM);
        task.setCreatedAt(OffsetDateTime.now());
        task.setAiFlagged(false);
        task.setBlockedAt(null);
        task.setDeliveredAt(null);
        return task;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
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

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
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

    public Integer getExpectedHours() {
        return expectedHours;
    }

    public void setExpectedHours(Integer expectedHours) {
        this.expectedHours = expectedHours;
    }

    public List<UUID> getAssigneeIds() {
        return assigneeIds;
    }

    public void setAssigneeIds(List<UUID> assigneeIds) {
        this.assigneeIds = assigneeIds;
    }
}
