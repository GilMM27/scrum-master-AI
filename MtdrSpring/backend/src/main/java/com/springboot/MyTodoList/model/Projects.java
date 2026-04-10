package com.springboot.MyTodoList.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/*
    Representation of the PROJECTS table that exists already
    in the autonomous database
*/
@Entity
@Table(name = "PROJECTS")
public class Projects {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "PROJECT_ID")
    UUID projectId;
    @Column(name = "NAME", nullable = false)
    String name;
    @Lob
    @Column(name = "DESCRIPTION")
    String description;
    @Column(name = "CREATED_AT")
    OffsetDateTime createdAt;

    public Projects() {
    }

    public Projects(UUID projectId, String name, String description, OffsetDateTime createdAt) {
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Projects{" +
                "projectId=" + projectId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
