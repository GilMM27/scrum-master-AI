package com.springboot.MyTodoList.dto;

public class TaskStatsResponse {
    private long totalProjectTasks;
    private long totalCurrentSprintTasks;
    private long totalCompletedTasks;
    private long totalReviewTasks;
    private long totalBlockedTasks;

    public TaskStatsResponse() {
    }

    public TaskStatsResponse(long totalProjectTasks, long totalCurrentSprintTasks, long totalCompletedTasks, long totalReviewTasks, long totalBlockedTasks) {
        this.totalProjectTasks = totalProjectTasks;
        this.totalCurrentSprintTasks = totalCurrentSprintTasks;
        this.totalCompletedTasks = totalCompletedTasks;
        this.totalReviewTasks = totalReviewTasks;
        this.totalBlockedTasks = totalBlockedTasks;
    }

    public long getTotalProjectTasks() {
        return totalProjectTasks;
    }

    public void setTotalProjectTasks(long totalProjectTasks) {
        this.totalProjectTasks = totalProjectTasks;
    }

    public long getTotalCurrentSprintTasks() {
        return totalCurrentSprintTasks;
    }

    public void setTotalCurrentSprintTasks(long totalCurrentSprintTasks) {
        this.totalCurrentSprintTasks = totalCurrentSprintTasks;
    }

    public long getTotalCompletedTasks() {
        return totalCompletedTasks;
    }

    public void setTotalCompletedTasks(long totalCompletedTasks) {
        this.totalCompletedTasks = totalCompletedTasks;
    }

    public long getTotalReviewTasks() {
        return totalReviewTasks;
    }

    public void setTotalReviewTasks(long totalReviewTasks) {
        this.totalReviewTasks = totalReviewTasks;
    }

    public long getTotalBlockedTasks() {
        return totalBlockedTasks;
    }

    public void setTotalBlockedTasks(long totalBlockedTasks) {
        this.totalBlockedTasks = totalBlockedTasks;
    }
}
