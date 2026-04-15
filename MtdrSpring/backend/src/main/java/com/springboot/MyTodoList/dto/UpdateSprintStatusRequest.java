package com.springboot.MyTodoList.dto;

import java.time.LocalDate;

public class UpdateSprintStatusRequest {
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;

    public UpdateSprintStatusRequest() {}

    public UpdateSprintStatusRequest(String status, LocalDate startDate, LocalDate endDate) {
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
