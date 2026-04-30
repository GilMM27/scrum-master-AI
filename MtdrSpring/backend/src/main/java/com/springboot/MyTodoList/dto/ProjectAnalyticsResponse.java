package com.springboot.MyTodoList.dto;

import java.util.List;

public class ProjectAnalyticsResponse {
    private Double avgLeadTimeDays;
    private Double avgCycleTimeDays;
    private Double completionRate;
    private long blockedTasksCount;
    private long delayedTasksCount;
    private List<VelocityDataPoint> velocityBySprint;
    private List<HistogramBucket> leadTimeHistogram;
    private Double leadTimeMean;
    private List<HistogramBucket> cycleTimeHistogram;
    private Double cycleTimeMean;
    private List<TasksDoneBySprintRow> tasksDoneBySprint;
    private List<BurndownDataPoint> burndownData;

    public ProjectAnalyticsResponse() {}

    public ProjectAnalyticsResponse(
            Double avgLeadTimeDays,
            Double avgCycleTimeDays,
            Double completionRate,
            long blockedTasksCount,
            long delayedTasksCount,
            List<VelocityDataPoint> velocityBySprint,
            List<HistogramBucket> leadTimeHistogram,
            Double leadTimeMean,
            List<HistogramBucket> cycleTimeHistogram,
            Double cycleTimeMean,
            List<TasksDoneBySprintRow> tasksDoneBySprint,
            List<BurndownDataPoint> burndownData) {
        this.avgLeadTimeDays = avgLeadTimeDays;
        this.avgCycleTimeDays = avgCycleTimeDays;
        this.completionRate = completionRate;
        this.blockedTasksCount = blockedTasksCount;
        this.delayedTasksCount = delayedTasksCount;
        this.velocityBySprint = velocityBySprint;
        this.leadTimeHistogram = leadTimeHistogram;
        this.leadTimeMean = leadTimeMean;
        this.cycleTimeHistogram = cycleTimeHistogram;
        this.cycleTimeMean = cycleTimeMean;
        this.tasksDoneBySprint = tasksDoneBySprint;
        this.burndownData = burndownData;
    }

    public Double getAvgLeadTimeDays() { return avgLeadTimeDays; }
    public void setAvgLeadTimeDays(Double avgLeadTimeDays) { this.avgLeadTimeDays = avgLeadTimeDays; }

    public Double getAvgCycleTimeDays() { return avgCycleTimeDays; }
    public void setAvgCycleTimeDays(Double avgCycleTimeDays) { this.avgCycleTimeDays = avgCycleTimeDays; }

    public Double getCompletionRate() { return completionRate; }
    public void setCompletionRate(Double completionRate) { this.completionRate = completionRate; }

    public long getBlockedTasksCount() { return blockedTasksCount; }
    public void setBlockedTasksCount(long blockedTasksCount) { this.blockedTasksCount = blockedTasksCount; }

    public long getDelayedTasksCount() { return delayedTasksCount; }
    public void setDelayedTasksCount(long delayedTasksCount) { this.delayedTasksCount = delayedTasksCount; }

    public List<VelocityDataPoint> getVelocityBySprint() { return velocityBySprint; }
    public void setVelocityBySprint(List<VelocityDataPoint> velocityBySprint) { this.velocityBySprint = velocityBySprint; }

    public List<HistogramBucket> getLeadTimeHistogram() { return leadTimeHistogram; }
    public void setLeadTimeHistogram(List<HistogramBucket> leadTimeHistogram) { this.leadTimeHistogram = leadTimeHistogram; }

    public Double getLeadTimeMean() { return leadTimeMean; }
    public void setLeadTimeMean(Double leadTimeMean) { this.leadTimeMean = leadTimeMean; }

    public List<HistogramBucket> getCycleTimeHistogram() { return cycleTimeHistogram; }
    public void setCycleTimeHistogram(List<HistogramBucket> cycleTimeHistogram) { this.cycleTimeHistogram = cycleTimeHistogram; }

    public Double getCycleTimeMean() { return cycleTimeMean; }
    public void setCycleTimeMean(Double cycleTimeMean) { this.cycleTimeMean = cycleTimeMean; }

    public List<TasksDoneBySprintRow> getTasksDoneBySprint() { return tasksDoneBySprint; }
    public void setTasksDoneBySprint(List<TasksDoneBySprintRow> tasksDoneBySprint) { this.tasksDoneBySprint = tasksDoneBySprint; }

    public List<BurndownDataPoint> getBurndownData() { return burndownData; }
    public void setBurndownData(List<BurndownDataPoint> burndownData) { this.burndownData = burndownData; }
}
