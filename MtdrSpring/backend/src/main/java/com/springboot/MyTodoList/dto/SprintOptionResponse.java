package com.springboot.MyTodoList.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.springboot.MyTodoList.model.SprintStatus;

public class SprintOptionResponse {
    private UUID sprintId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private SprintStatus status;

    public SprintOptionResponse() {
    }

    public SprintOptionResponse(UUID sprintId, String name, LocalDate startDate, LocalDate endDate, SprintStatus status) {
        this.sprintId = sprintId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public UUID getSprintId() {
        return sprintId;
    }

    public void setSprintId(UUID sprintId) {
        this.sprintId = sprintId;
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
