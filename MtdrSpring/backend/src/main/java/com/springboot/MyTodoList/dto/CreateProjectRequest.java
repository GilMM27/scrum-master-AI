package com.springboot.MyTodoList.dto;

import com.springboot.MyTodoList.model.Projects;

import java.time.OffsetDateTime;

public class CreateProjectRequest {
    private String name;
    private String description;

    public Projects toEntity() {
        Projects project = new Projects();
        project.setName(this.name);
        project.setDescription(this.description);
        project.setCreatedAt(OffsetDateTime.now());
        return project;
    }

    public CreateProjectRequest() {
    }

    public CreateProjectRequest(String name, String description) {
        this.name = name;
        this.description = description;
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
}
