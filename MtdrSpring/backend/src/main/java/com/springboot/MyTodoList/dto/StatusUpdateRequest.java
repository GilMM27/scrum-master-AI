package com.springboot.MyTodoList.dto;

import com.springboot.MyTodoList.model.TaskStatus;

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
