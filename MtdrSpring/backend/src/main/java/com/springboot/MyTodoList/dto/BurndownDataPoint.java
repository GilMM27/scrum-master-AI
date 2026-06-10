package com.springboot.MyTodoList.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BurndownDataPoint {
    private String date;
    private Double remaining;
    private double ideal;

    public BurndownDataPoint() {}

    public BurndownDataPoint(String date, Double remaining, double ideal) {
        this.date = date;
        this.remaining = remaining;
        this.ideal = ideal;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Double getRemaining() { return remaining; }
    public void setRemaining(Double remaining) { this.remaining = remaining; }

    public double getIdeal() { return ideal; }
    public void setIdeal(double ideal) { this.ideal = ideal; }
}
