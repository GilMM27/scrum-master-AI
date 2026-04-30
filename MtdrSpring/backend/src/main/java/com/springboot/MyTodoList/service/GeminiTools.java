package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.*;
import com.springboot.MyTodoList.model.Sprints;
import com.springboot.MyTodoList.model.SprintStatus;
import com.springboot.MyTodoList.model.Projects;
import com.springboot.MyTodoList.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GeminiTools {
    private static final Logger logger = LoggerFactory.getLogger(GeminiTools.class);

    private final SprintService sprintService;
    private final ProjectService projectService;
    private final TasksService tasksService;
    private final ProjectMemberService projectMemberService;

    public GeminiTools(SprintService sprintService, ProjectService projectService, TasksService tasksService, ProjectMemberService projectMemberService) {
        this.sprintService = sprintService;
        this.projectService = projectService;
        this.tasksService = tasksService;
        this.projectMemberService = projectMemberService;
    }

    @Tool(name = "getProjects", description = "Get all projects")
    public List<Projects> getProjects() {
        logger.info("Tool [getProjects] called");
        return projectService.getAllProjects();
    }

    @Tool(name = "getActiveSprints", description = "Get all active sprints for a project")
    public List<SprintOptionResponse> getActiveSprints(UUID projectId) {
        logger.info("Tool [getActiveSprints] called for projectId: {}", projectId);
        return sprintService.getAvailableSprints(projectId).stream()
                .filter(s -> s.getStatus() == SprintStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    @Tool(name = "getAllSprints", description = "Get all sprints for a project")
    public List<SprintOptionResponse> getAllSprints(UUID projectId) {
        logger.info("Tool [getAllSprints] called for projectId: {}", projectId);
        return sprintService.getProjectSprints(projectId);
    }

    @Tool(name = "getTasksBySprint", description = "Get all tasks for a specific sprint")
    public List<TaskSummaryResponse> getTasksBySprint(UUID sprintId) {
        logger.info("Tool [getTasksBySprint] called for sprintId: {}", sprintId);
        return tasksService.getTasksBySprint(sprintId);
    }

    @Tool(name = "getTasksByProject", description = "Get all tasks for a specific project")
    public List<TaskSummaryResponse> getTasksByProject(UUID projectId) {
        logger.info("Tool [getTasksByProject] called for projectId: {}", projectId);
        return tasksService.getTasksByProject(projectId);
    }

    @Tool(name = "getBacklogByProject", description = "Get tasks in the backlog (not assigned to any sprint) for a project")
    public List<TaskSummaryResponse> getBacklogByProject(UUID projectId) {
        logger.info("Tool [getBacklogByProject] called for projectId: {}", projectId);
        return tasksService.getBacklogByProject(projectId);
    }

    @Tool(name = "getTaskStats", description = "Get task statistics for a project (total, completed, blocked, etc.)")
    public ResponseEntity<?> getTaskStats(UUID projectId) {
        logger.info("Tool [getTaskStats] called for projectId: {}", projectId);
        return tasksService.getTaskStats(projectId);
    }

    @Tool(name = "getSprintSummary", description = "Get a summary of a sprint performance and status")
    public ResponseEntity<?> getSprintSummary(UUID sprintId) {
        logger.info("Tool [getSprintSummary] called for sprintId: {}", sprintId);
        return sprintService.getSprintSummary(sprintId);
    }

    @Tool(name = "createTask", description = "Create a new task in a project")
    public ResponseEntity<?> createTask(CreateTaskRequest request) {
        logger.info("Tool [createTask] called for project: {}", request.getProjectId());
        return tasksService.createTask(request);
    }

    @Tool(name = "updateTask", description = "Update an existing task")
    public ResponseEntity<?> updateTask(UUID taskId, UpdateTaskRequest request) {
        logger.info("Tool [updateTask] called for taskId: {}", taskId);
        return tasksService.updateTask(taskId, request);
    }

    @Tool(name = "updateTaskStatus", description = "Update the status of a task")
    public ResponseEntity<?> updateTaskStatus(UUID taskId, TaskStatus status) {
        logger.info("Tool [updateTaskStatus] called for taskId: {} to status: {}", taskId, status);
        return tasksService.updateStatus(taskId, status);
    }

    @Tool(name = "getProjectMembers", description = "Get all members of a project")
    public List<ProjectDeveloperResponse> getProjectMembers(UUID projectId) {
        logger.info("Tool [getProjectMembers] called for projectId: {}", projectId);
        return projectMemberService.getActiveDevelopersByProject(projectId);
    }

    @Tool(name = "getMyTasks", description = "Get tasks assigned to the current user")
    public List<TaskSummaryResponse> getMyTasks(UUID userId) {
        logger.info("Tool [getMyTasks] called for userId: {}", userId);
        return tasksService.getAllTasks().stream()
                .filter(t -> t.getAssignees().stream().anyMatch(a -> a.getUserId().equals(userId)))
                .collect(Collectors.toList());
    }

    @Tool(name = "createSprint", description = "Create a new sprint for a project")
    public ResponseEntity<?> createSprint(CreateSprintRequest request) {
        logger.info("Tool [createSprint] called for project: {}", request.getProjectId());
        return sprintService.createSprint(request);
    }
}
