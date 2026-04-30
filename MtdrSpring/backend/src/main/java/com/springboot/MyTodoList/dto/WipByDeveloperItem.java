package com.springboot.MyTodoList.dto;

import java.util.UUID;

public class WipByDeveloperItem {
    private UUID userId;
    private String username;
    private long inProgressTasks;
    private long reviewTasks;
    private long totalActiveTasks;

    public WipByDeveloperItem() {}

    public WipByDeveloperItem(UUID userId, String username, long inProgressTasks, long reviewTasks, long totalActiveTasks) {
        this.userId = userId;
        this.username = username;
        this.inProgressTasks = inProgressTasks;
        this.reviewTasks = reviewTasks;
        this.totalActiveTasks = totalActiveTasks;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public long getInProgressTasks() { return inProgressTasks; }
    public void setInProgressTasks(long inProgressTasks) { this.inProgressTasks = inProgressTasks; }

    public long getReviewTasks() { return reviewTasks; }
    public void setReviewTasks(long reviewTasks) { this.reviewTasks = reviewTasks; }

    public long getTotalActiveTasks() { return totalActiveTasks; }
    public void setTotalActiveTasks(long totalActiveTasks) { this.totalActiveTasks = totalActiveTasks; }
}
