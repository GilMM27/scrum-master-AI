package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Sprints;
import com.springboot.MyTodoList.model.SprintStatus;
import com.springboot.MyTodoList.model.Projects;
import com.springboot.MyTodoList.model.Tasks;
import com.springboot.MyTodoList.repository.SprintsRepository;
import com.springboot.MyTodoList.repository.ProjectsRepository;
import com.springboot.MyTodoList.repository.TasksRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GeminiTools {

    private final SprintsRepository sprintsRepository;
    private final ProjectsRepository projectsRepository;
    private final TasksRepository tasksRepository;

    public GeminiTools(SprintsRepository sprintsRepository, ProjectsRepository projectsRepository, TasksRepository tasksRepository) {
        this.sprintsRepository = sprintsRepository;
        this.projectsRepository = projectsRepository;
        this.tasksRepository = tasksRepository;
    }

    @Tool(name = "getProjects", description = "Get all projects")
    public List<Projects> getProjects() {
        return projectsRepository.findAll();
    }

    @Tool(name = "getActiveSprints", description = "Get all active sprints for a project")
    public List<Sprints> getActiveSprints(UUID projectId) {
        return sprintsRepository.findByProjectId(projectId).stream()
                .filter(s -> s.getStatus() == SprintStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    @Tool(name = "getAllSprints", description = "Get all sprints for a project")
    public List<Sprints> getAllSprints(UUID projectId) {
        return sprintsRepository.findByProjectId(projectId);
    }

    @Tool(name = "getTasksBySprint", description = "Get all tasks for a specific sprint")
    public List<Tasks> getTasksBySprint(UUID sprintId) {
        return tasksRepository.findBySprintId(sprintId);
    }

    @Tool(name = "getTasksByProject", description = "Get all tasks for a specific project")
    public List<Tasks> getTasksByProject(UUID projectId) {
        return tasksRepository.findByProjectId(projectId);
    }
}
