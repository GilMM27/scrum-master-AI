package com.springboot.MyTodoList.dto;

public class HistogramBucket {
    private int days;
    private long count;

    public HistogramBucket() {}

    public HistogramBucket(int days, long count) {
        this.days = days;
        this.count = count;
    }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
