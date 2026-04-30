package com.springboot.MyTodoList.dto;

import java.util.UUID;

public class VelocityDataPoint {
    private UUID sprintId;
    private String sprintName;
    private int completedStoryPoints;
    private long completedTasks;
    private long totalTasks;

    public VelocityDataPoint() {}

    public VelocityDataPoint(UUID sprintId, String sprintName, int completedStoryPoints, long completedTasks, long totalTasks) {
        this.sprintId = sprintId;
        this.sprintName = sprintName;
        this.completedStoryPoints = completedStoryPoints;
        this.completedTasks = completedTasks;
        this.totalTasks = totalTasks;
    }

    public UUID getSprintId() { return sprintId; }
    public void setSprintId(UUID sprintId) { this.sprintId = sprintId; }

    public String getSprintName() { return sprintName; }
    public void setSprintName(String sprintName) { this.sprintName = sprintName; }

    public int getCompletedStoryPoints() { return completedStoryPoints; }
    public void setCompletedStoryPoints(int completedStoryPoints) { this.completedStoryPoints = completedStoryPoints; }

    public long getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(long completedTasks) { this.completedTasks = completedTasks; }

    public long getTotalTasks() { return totalTasks; }
    public void setTotalTasks(long totalTasks) { this.totalTasks = totalTasks; }
}
