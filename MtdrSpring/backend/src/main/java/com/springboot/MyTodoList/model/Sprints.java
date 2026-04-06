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
    UUID sprint_id;
    @Convert(converter = UUIDConverter.class)
    @Column(name = "PROJECT_ID", nullable = false)
    UUID project_id;
    @Column(name = "NAME", nullable = false)
    String name;
    @Column(name = "START_DATE")
    LocalDate start_date;
    @Column(name = "END_DATE")
    LocalDate end_date;
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    SprintStatus status;

    public Sprints() {
    }

    public Sprints(UUID sprint_id, UUID project_id, String name, LocalDate start_date, LocalDate end_date, SprintStatus status) {
        this.sprint_id = sprint_id;
        this.project_id = project_id;
        this.name = name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.status = status;
    }

    public UUID getSprintId() {
        return sprint_id;
    }

    public void setSprintId(UUID sprint_id) {
        this.sprint_id = sprint_id;
    }

    public UUID getProjectId() {
        return project_id;
    }

    public void setProjectId(UUID project_id) {
        this.project_id = project_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return start_date;
    }

    public void setStartDate(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEndDate() {
        return end_date;
    }

    public void setEndDate(LocalDate end_date) {
        this.end_date = end_date;
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
                "sprint_id=" + sprint_id +
                ", project_id=" + project_id +
                ", name='" + name + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", status='" + status + '\'' +
                '}';
    }
}