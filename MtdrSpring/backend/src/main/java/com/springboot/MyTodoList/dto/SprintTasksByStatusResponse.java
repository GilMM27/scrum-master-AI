package com.springboot.MyTodoList.dto;

import java.util.List;

public class SprintTasksByStatusResponse {
    private List<TaskSummaryResponse> todo;
    private List<TaskSummaryResponse> inProgress;
    private List<TaskSummaryResponse> review;
    private List<TaskSummaryResponse> blocked;
    private List<TaskSummaryResponse> done;

    public SprintTasksByStatusResponse() {
    }

    public SprintTasksByStatusResponse(
            List<TaskSummaryResponse> todo,
            List<TaskSummaryResponse> inProgress,
            List<TaskSummaryResponse> review,
            List<TaskSummaryResponse> blocked,
            List<TaskSummaryResponse> done) {
        this.todo = todo;
        this.inProgress = inProgress;
        this.review = review;
        this.blocked = blocked;
        this.done = done;
    }

    public List<TaskSummaryResponse> getTodo() {
        return todo;
    }

    public void setTodo(List<TaskSummaryResponse> todo) {
        this.todo = todo;
    }

    public List<TaskSummaryResponse> getInProgress() {
        return inProgress;
    }

    public void setInProgress(List<TaskSummaryResponse> inProgress) {
        this.inProgress = inProgress;
    }

    public List<TaskSummaryResponse> getReview() {
        return review;
    }

    public void setReview(List<TaskSummaryResponse> review) {
        this.review = review;
    }

    public List<TaskSummaryResponse> getBlocked() {
        return blocked;
    }

    public void setBlocked(List<TaskSummaryResponse> blocked) {
        this.blocked = blocked;
    }

    public List<TaskSummaryResponse> getDone() {
        return done;
    }

    public void setDone(List<TaskSummaryResponse> done) {
        this.done = done;
    }
}
