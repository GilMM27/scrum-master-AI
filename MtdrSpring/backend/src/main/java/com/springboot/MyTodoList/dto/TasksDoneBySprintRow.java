package com.springboot.MyTodoList.dto;

import java.util.Map;
import java.util.UUID;

public class TasksDoneBySprintRow {
    private UUID sprintId;
    private String sprintName;
    /** username → number of DONE tasks */
    private Map<String, Long> tasksDoneByUser;

    public TasksDoneBySprintRow() {}

    public TasksDoneBySprintRow(UUID sprintId, String sprintName, Map<String, Long> tasksDoneByUser) {
        this.sprintId = sprintId;
        this.sprintName = sprintName;
        this.tasksDoneByUser = tasksDoneByUser;
    }

    public UUID getSprintId() { return sprintId; }
    public void setSprintId(UUID sprintId) { this.sprintId = sprintId; }

    public String getSprintName() { return sprintName; }
    public void setSprintName(String sprintName) { this.sprintName = sprintName; }

    public Map<String, Long> getTasksDoneByUser() { return tasksDoneByUser; }
    public void setTasksDoneByUser(Map<String, Long> tasksDoneByUser) { this.tasksDoneByUser = tasksDoneByUser; }
}
