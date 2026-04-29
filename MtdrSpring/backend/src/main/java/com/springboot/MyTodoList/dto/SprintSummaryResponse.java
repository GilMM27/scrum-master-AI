package com.springboot.MyTodoList.dto;

public class SprintSummaryResponse {
    private String sprintName;
    private long totalTasks;
    private long completedTasks;
    private long inProgressTasks;
    private long reviewTasks;
    private long blockedTasks;
    private long toDoTasks;
    private double totalEstimatedHours;
    private Double totalActualHours;

    public SprintSummaryResponse() {}

    public SprintSummaryResponse(
            String sprintName,
            long totalTasks,
            long completedTasks,
            long inProgressTasks,
            long reviewTasks,
            long blockedTasks,
            long toDoTasks,
            double totalEstimatedHours,
            Double totalActualHours) {
        this.sprintName = sprintName;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.inProgressTasks = inProgressTasks;
        this.reviewTasks = reviewTasks;
        this.blockedTasks = blockedTasks;
        this.toDoTasks = toDoTasks;
        this.totalEstimatedHours = totalEstimatedHours;
        this.totalActualHours = totalActualHours;
    }

    public String getSprintName() { return sprintName; }
    public void setSprintName(String sprintName) { this.sprintName = sprintName; }

    public long getTotalTasks() { return totalTasks; }
    public void setTotalTasks(long totalTasks) { this.totalTasks = totalTasks; }

    public long getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(long completedTasks) { this.completedTasks = completedTasks; }

    public long getInProgressTasks() { return inProgressTasks; }
    public void setInProgressTasks(long inProgressTasks) { this.inProgressTasks = inProgressTasks; }

    public long getReviewTasks() { return reviewTasks; }
    public void setReviewTasks(long reviewTasks) { this.reviewTasks = reviewTasks; }

    public long getBlockedTasks() { return blockedTasks; }
    public void setBlockedTasks(long blockedTasks) { this.blockedTasks = blockedTasks; }

    public long getToDoTasks() { return toDoTasks; }
    public void setToDoTasks(long toDoTasks) { this.toDoTasks = toDoTasks; }

    public double getTotalEstimatedHours() { return totalEstimatedHours; }
    public void setTotalEstimatedHours(double totalEstimatedHours) { this.totalEstimatedHours = totalEstimatedHours; }

    public Double getTotalActualHours() { return totalActualHours; }
    public void setTotalActualHours(Double totalActualHours) { this.totalActualHours = totalActualHours; }
}
