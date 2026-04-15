package com.springboot.MyTodoList.dto;

import com.springboot.MyTodoList.model.SprintStatus;
import com.springboot.MyTodoList.model.Sprints;

import java.time.LocalDate;
import java.util.UUID;

public class CreateSprintRequest {
    private UUID projectId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private SprintStatus status;

    public Sprints toEntity() {
        Sprints sprint = new Sprints();
        sprint.setProjectId(this.projectId);
        sprint.setName(this.name);
        sprint.setStartDate(this.startDate);
        sprint.setEndDate(this.endDate);
        sprint.setStatus(this.status != null ? this.status : SprintStatus.PLANNED);
        return sprint;
    }

    public CreateSprintRequest() {
    }

    public CreateSprintRequest(UUID projectId, String name, LocalDate startDate, LocalDate endDate, SprintStatus status) {
        this.projectId = projectId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public SprintStatus getStatus() {
        return status;
    }

    public void setStatus(SprintStatus status) {
        this.status = status;
    }
}
