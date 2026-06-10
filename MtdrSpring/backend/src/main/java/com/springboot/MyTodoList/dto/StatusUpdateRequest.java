package com.springboot.MyTodoList.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springboot.MyTodoList.model.TaskStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusUpdateRequest {
    private TaskStatus status;

    public StatusUpdateRequest() {
    }

    public StatusUpdateRequest(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
