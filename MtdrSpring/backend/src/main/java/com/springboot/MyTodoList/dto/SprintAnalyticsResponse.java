package com.springboot.MyTodoList.dto;

import java.util.List;
import java.util.UUID;

public class SprintAnalyticsResponse {
    private UUID sprintId;
    private String sprintName;
    private String startDate;
    private String endDate;
    private Double sprintAccomplishment;
    private Double avgCycleTimeDays;
    private long delayedTasksCount;
    private List<WipByDeveloperItem> wipByDeveloper;
    private List<BurndownDataPoint> burndownData;
    private List<HistogramBucket> leadTimeHistogram;
    private Double leadTimeMean;
    private List<HistogramBucket> cycleTimeHistogram;
    private Double cycleTimeMean;
    private List<TasksDoneBySprintRow> tasksDoneBySprint;
    private long blockedTasksCount;

    public SprintAnalyticsResponse() {}

    public SprintAnalyticsResponse(
            UUID sprintId,
            String sprintName,
            String startDate,
            String endDate,
            Double sprintAccomplishment,
            Double avgCycleTimeDays,
            long delayedTasksCount,
            List<WipByDeveloperItem> wipByDeveloper,
            List<BurndownDataPoint> burndownData,
            List<HistogramBucket> leadTimeHistogram,
            Double leadTimeMean,
            List<HistogramBucket> cycleTimeHistogram,
            Double cycleTimeMean,
            List<TasksDoneBySprintRow> tasksDoneBySprint,
            long blockedTasksCount) {
        this.sprintId = sprintId;
        this.sprintName = sprintName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sprintAccomplishment = sprintAccomplishment;
        this.avgCycleTimeDays = avgCycleTimeDays;
        this.delayedTasksCount = delayedTasksCount;
        this.wipByDeveloper = wipByDeveloper;
        this.burndownData = burndownData;
        this.leadTimeHistogram = leadTimeHistogram;
        this.leadTimeMean = leadTimeMean;
        this.cycleTimeHistogram = cycleTimeHistogram;
        this.cycleTimeMean = cycleTimeMean;
        this.tasksDoneBySprint = tasksDoneBySprint;
        this.blockedTasksCount = blockedTasksCount;
    }

    public UUID getSprintId() { return sprintId; }
    public void setSprintId(UUID sprintId) { this.sprintId = sprintId; }

    public String getSprintName() { return sprintName; }
    public void setSprintName(String sprintName) { this.sprintName = sprintName; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public Double getSprintAccomplishment() { return sprintAccomplishment; }
    public void setSprintAccomplishment(Double sprintAccomplishment) { this.sprintAccomplishment = sprintAccomplishment; }

    public Double getAvgCycleTimeDays() { return avgCycleTimeDays; }
    public void setAvgCycleTimeDays(Double avgCycleTimeDays) { this.avgCycleTimeDays = avgCycleTimeDays; }

    public long getDelayedTasksCount() { return delayedTasksCount; }
    public void setDelayedTasksCount(long delayedTasksCount) { this.delayedTasksCount = delayedTasksCount; }

    public List<WipByDeveloperItem> getWipByDeveloper() { return wipByDeveloper; }
    public void setWipByDeveloper(List<WipByDeveloperItem> wipByDeveloper) { this.wipByDeveloper = wipByDeveloper; }

    public List<BurndownDataPoint> getBurndownData() { return burndownData; }
    public void setBurndownData(List<BurndownDataPoint> burndownData) { this.burndownData = burndownData; }

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

    public long getBlockedTasksCount() { return blockedTasksCount; }
    public void setBlockedTasksCount(long blockedTasksCount) { this.blockedTasksCount = blockedTasksCount; }
}
