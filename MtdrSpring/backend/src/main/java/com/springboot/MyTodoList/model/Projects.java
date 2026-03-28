package com.springboot.MyTodoList.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/*
    Representation of the PROJECTS table that exists already
    in the autonomous database
 */
@Entity
@Table(name = "PROJECTS")
public class Projects {
    @Id
    @Column(name = "PROJECT_ID", nullable = false)
    int project_id;
    @Column(name = "NAME", nullable = false)
    String name;
    @Lob
    @Column(name = "DESCRIPTION")
    String description;
    @Column(name = "CREATED_AT")
    OffsetDateTime created_at;

    public Projects() {
    }

    public Projects(int project_id, String name, String description, OffsetDateTime created_at) {
        this.project_id = project_id;
        this.name = name;
        this.description = description;
        this.created_at = created_at;
    }

    public int getProjectId() {
        return project_id;
    }

    public void setProjectId(int project_id) {
        this.project_id = project_id;
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
        return created_at;
    }

    public void setCreatedAt(OffsetDateTime created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Projects{" +
                "project_id=" + project_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", created_at=" + created_at +
                '}';
    }
}