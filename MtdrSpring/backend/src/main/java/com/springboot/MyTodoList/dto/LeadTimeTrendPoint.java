package com.springboot.MyTodoList.dto;

public class LeadTimeTrendPoint {
    private String weekLabel;
    private Double avgLeadTimeDays;
    private long taskCount;

    public LeadTimeTrendPoint() {}

    public LeadTimeTrendPoint(String weekLabel, Double avgLeadTimeDays, long taskCount) {
        this.weekLabel = weekLabel;
        this.avgLeadTimeDays = avgLeadTimeDays;
        this.taskCount = taskCount;
    }

    public String getWeekLabel() { return weekLabel; }
    public void setWeekLabel(String weekLabel) { this.weekLabel = weekLabel; }

    public Double getAvgLeadTimeDays() { return avgLeadTimeDays; }
    public void setAvgLeadTimeDays(Double avgLeadTimeDays) { this.avgLeadTimeDays = avgLeadTimeDays; }

    public long getTaskCount() { return taskCount; }
    public void setTaskCount(long taskCount) { this.taskCount = taskCount; }
}
