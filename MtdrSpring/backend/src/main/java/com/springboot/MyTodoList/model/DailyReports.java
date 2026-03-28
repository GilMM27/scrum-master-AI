package com.springboot.MyTodoList.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;

/*
    Representation of the DAILY_REPORTS table that exists already
    in the autonomous database
 */
@Entity
@Table(name = "DAILY_REPORTS")
public class DailyReports {
    @Id
    @Column(name = "REPORT_ID", nullable = false)
    int report_id;
    @Column(name = "PROJECT_ID")
    int project_id;
    @Column(name = "REPORT_DATE")
    LocalDate report_date;
    @Lob
    @Column(name = "CONTENT_TLDR")
    String content_tldr;
    @Lob
    @Column(name = "MINUTES_CONTENT")
    String minutes_content;

    public DailyReports() {
    }

    public DailyReports(int report_id, int project_id, LocalDate report_date, String content_tldr, String minutes_content) {
        this.report_id = report_id;
        this.project_id = project_id;
        this.report_date = report_date;
        this.content_tldr = content_tldr;
        this.minutes_content = minutes_content;
    }

    public int getReportId() {
        return report_id;
    }

    public void setReportId(int report_id) {
        this.report_id = report_id;
    }

    public int getProjectId() {
        return project_id;
    }

    public void setProjectId(int project_id) {
        this.project_id = project_id;
    }

    public LocalDate getReportDate() {
        return report_date;
    }

    public void setReportDate(LocalDate report_date) {
        this.report_date = report_date;
    }

    public String getContentTldr() {
        return content_tldr;
    }

    public void setContentTldr(String content_tldr) {
        this.content_tldr = content_tldr;
    }

    public String getMinutesContent() {
        return minutes_content;
    }

    public void setMinutesContent(String minutes_content) {
        this.minutes_content = minutes_content;
    }

    @Override
    public String toString() {
        return "DailyReports{" +
                "report_id=" + report_id +
                ", project_id=" + project_id +
                ", report_date=" + report_date +
                ", content_tldr='" + content_tldr + '\'' +
                ", minutes_content='" + minutes_content + '\'' +
                '}';
    }
}