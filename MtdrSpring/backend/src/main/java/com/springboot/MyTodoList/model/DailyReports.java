package com.springboot.MyTodoList.model;

import com.springboot.MyTodoList.util.UUIDConverter;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

/*
    Representation of the DAILY_REPORTS table that exists already
    in the autonomous database
 */
@Entity
@Table(name = "DAILY_REPORTS")
public class DailyReports {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "REPORT_ID")
    UUID reportId;
    @Convert(converter = UUIDConverter.class)
    @Column(name = "PROJECT_ID", nullable = false)
    UUID projectId;
    @Column(name = "REPORT_DATE", nullable = false)
    LocalDate reportDate;
    @Lob
    @Column(name = "CONTENT_TLDR")
    String contentTldr;
    @Lob
    @Column(name = "MINUTES_CONTENT")
    String minutesContent;

    public DailyReports() {
    }

    public DailyReports(UUID reportId, UUID projectId, LocalDate reportDate, String contentTldr, String minutesContent) {
        this.reportId = reportId;
        this.projectId = projectId;
        this.reportDate = reportDate;
        this.contentTldr = contentTldr;
        this.minutesContent = minutesContent;
    }

    public UUID getReportId() {
        return reportId;
    }

    public void setReportId(UUID reportId) {
        this.reportId = reportId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public String getContentTldr() {
        return contentTldr;
    }

    public void setContentTldr(String contentTldr) {
        this.contentTldr = contentTldr;
    }

    public String getMinutesContent() {
        return minutesContent;
    }

    public void setMinutesContent(String minutesContent) {
        this.minutesContent = minutesContent;
    }

    @Override
    public String toString() {
        return "DailyReports{" +
                "reportId=" + reportId +
                ", projectId=" + projectId +
                ", reportDate=" + reportDate +
                ", contentTldr='" + contentTldr + '\'' +
                ", minutesContent='" + minutesContent + '\'' +
                '}';
    }
}