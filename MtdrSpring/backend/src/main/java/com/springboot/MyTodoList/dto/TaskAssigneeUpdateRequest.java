package com.springboot.MyTodoList.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskAssigneeUpdateRequest {
    private List<UUID> assigneeIds;

    public TaskAssigneeUpdateRequest() {
    }

    public List<UUID> getAssigneeIds() {
        return assigneeIds;
    }

    public void setAssigneeIds(List<UUID> assigneeIds) {
        this.assigneeIds = assigneeIds;
    }
}
