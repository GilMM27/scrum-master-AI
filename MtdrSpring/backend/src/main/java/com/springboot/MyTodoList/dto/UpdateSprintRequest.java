package com.springboot.MyTodoList.dto;

import java.time.LocalDate;

public class UpdateSprintRequest {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public UpdateSprintRequest() {}

    public UpdateSprintRequest(String name, LocalDate startDate, LocalDate endDate, String status) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
