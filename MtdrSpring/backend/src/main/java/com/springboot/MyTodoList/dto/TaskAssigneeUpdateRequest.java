package com.springboot.MyTodoList.dto;

import java.util.List;
import java.util.UUID;

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
