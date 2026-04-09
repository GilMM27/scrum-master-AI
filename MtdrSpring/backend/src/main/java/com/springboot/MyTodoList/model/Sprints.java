package com.springboot.MyTodoList.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import com.springboot.MyTodoList.util.UUIDConverter;

/*
    Representation of the SPRINTS table that exists already
    in the autonomous database
*/
@Entity
@Table(name = "SPRINTS")
public class Sprints {
    @Id
    @Convert(converter = UUIDConverter.class)
    @Column(name = "SPRINT_ID", nullable = false)
    UUID sprintId;
    @Convert(converter = UUIDConverter.class)
    @Column(name = "PROJECT_ID", nullable = false)
    UUID projectId;
    @Column(name = "NAME", nullable = false)
    String name;
    @Column(name = "START_DATE")
    LocalDate startDate;
    @Column(name = "END_DATE")
    LocalDate endDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    SprintStatus status;

    public Sprints() {
    }

    public Sprints(UUID sprintId, UUID projectId, String name, LocalDate startDate, LocalDate endDate, SprintStatus status) {
        this.sprintId = sprintId;
        this.projectId = projectId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public UUID getSprintId() {
        return sprintId;
    }

    public void setSprintId(UUID sprintId) {
        this.sprintId = sprintId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public SprintStatus getStatus() {
        return status;
    }

    public void setStatus(SprintStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Sprints{" +
                "sprintId=" + sprintId +
                ", projectId=" + projectId +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                '}';
    }
}
