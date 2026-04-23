package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.CreateTaskRequest;
import com.springboot.MyTodoList.dto.SprintTasksByStatusResponse;
import com.springboot.MyTodoList.dto.TaskAssigneeResponse;
import com.springboot.MyTodoList.dto.TaskAssigneeUpdateRequest;
import com.springboot.MyTodoList.dto.TaskDetailResponse;
import com.springboot.MyTodoList.dto.TaskStatsResponse;
import com.springboot.MyTodoList.dto.TaskSummaryResponse;
import com.springboot.MyTodoList.dto.UpdateTaskRequest;
import com.springboot.MyTodoList.exception.ResourceNotFoundException;
import com.springboot.MyTodoList.model.Projects;
import com.springboot.MyTodoList.model.SprintStatus;
import com.springboot.MyTodoList.model.Sprints;
import com.springboot.MyTodoList.model.TaskStatus;
import com.springboot.MyTodoList.model.Tasks;
import com.springboot.MyTodoList.model.TaskAssignments;
import com.springboot.MyTodoList.model.TaskAssignmentsId;
import com.springboot.MyTodoList.repository.ProjectsRepository;
import com.springboot.MyTodoList.repository.SprintsRepository;
import com.springboot.MyTodoList.repository.TaskAssignmentsRepository;
import com.springboot.MyTodoList.repository.TasksRepository;
import com.springboot.MyTodoList.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TasksService {

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private SprintsRepository sprintsRepository;

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TaskAssignmentsRepository taskAssignmentsRepository;

    private static final Map<TaskStatus, Set<TaskStatus>> VALID_TRANSITIONS = Map.of(
        TaskStatus.TO_DO, Set.of(TaskStatus.IN_PROGRESS, TaskStatus.BLOCKED),
        TaskStatus.IN_PROGRESS, Set.of(TaskStatus.REVIEW, TaskStatus.BLOCKED, TaskStatus.TO_DO),
        TaskStatus.REVIEW, Set.of(TaskStatus.DONE, TaskStatus.IN_PROGRESS),
        TaskStatus.BLOCKED, Set.of(TaskStatus.TO_DO),
        TaskStatus.DONE, Set.of()
    );

    public ResponseEntity<?> createTask(CreateTaskRequest request) {
        ResponseEntity<?> validationError = validateCreateOrUpdateRequest(
            request.getProjectId(),
            request.getTitle(),
            request.getStoryPoints(),
            request.getSprintId()
        );
        if (validationError != null) return validationError;

        Tasks task = request.toEntity();
        Tasks savedTask = tasksRepository.save(task);

        if (request.getAssigneeIds() != null && !request.getAssigneeIds().isEmpty()) {
            syncAssignments(savedTask.getTaskId(), request.getAssigneeIds());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(mapTaskDetailResponse(savedTask));
    }

    public List<TaskSummaryResponse> getTasksByProject(UUID projectId) {
        return tasksRepository.findByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(this::mapTaskSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<TaskSummaryResponse> getBacklogByProject(UUID projectId) {
        return tasksRepository.findByProjectIdAndSprintIdIsNullOrderByCreatedAtDesc(projectId).stream()
                .map(this::mapTaskSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<TaskSummaryResponse> getAllTasks() {
        return tasksRepository.findAll().stream()
                .map(this::mapTaskSummaryResponse)
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> getTaskById(UUID taskId) {
        return tasksRepository.findById(taskId)
                .<ResponseEntity<?>>map(task -> ResponseEntity.ok(mapTaskDetailResponse(task)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public List<TaskSummaryResponse> getTasksBySprint(UUID sprintId) {
        return tasksRepository.findBySprintIdOrderByCreatedAtDesc(sprintId).stream()
                .map(this::mapTaskSummaryResponse)
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> getSprintTasksByStatus(UUID sprintId) {
        if (!sprintsRepository.existsById(sprintId)) {
            return ResponseEntity.notFound().build();
        }

        List<TaskSummaryResponse> all = tasksRepository.findBySprintIdOrderByCreatedAtDesc(sprintId).stream()
                .map(this::mapTaskSummaryResponse)
                .collect(Collectors.toList());

        SprintTasksByStatusResponse response = new SprintTasksByStatusResponse(
                filterByStatus(all, TaskStatus.TO_DO),
                filterByStatus(all, TaskStatus.IN_PROGRESS),
                filterByStatus(all, TaskStatus.REVIEW),
                filterByStatus(all, TaskStatus.BLOCKED),
                filterByStatus(all, TaskStatus.DONE)
        );

        return ResponseEntity.ok(response);
    }

    private List<TaskSummaryResponse> filterByStatus(List<TaskSummaryResponse> tasks, TaskStatus status) {
        return tasks.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> updateTask(UUID taskId, UpdateTaskRequest request) {
        Optional<Tasks> taskOpt = tasksRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tasks task = taskOpt.get();

        ResponseEntity<?> validationError = validateCreateOrUpdateRequest(
            task.getProjectId(),
            request.getTitle(),
            request.getStoryPoints(),
            request.getSprintId()
        );
        if (validationError != null) return validationError;

        if (request.getTitle() != null) task.setTitle(request.getTitle().trim());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        task.setSprintId(request.getSprintId());
        if (request.getStoryPoints() != null) task.setStoryPoints(request.getStoryPoints());

        Tasks savedTask = tasksRepository.save(task);

        if (request.getAssigneeIds() != null) {
            syncAssignments(savedTask.getTaskId(), request.getAssigneeIds());
        }

        return ResponseEntity.ok(mapTaskDetailResponse(savedTask));
    }

    public ResponseEntity<?> updateStatus(UUID taskId, TaskStatus newStatus) {
        if (newStatus == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
        }

        Optional<Tasks> taskOpt = tasksRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tasks task = taskOpt.get();
        TaskStatus currentStatus = task.getStatus();

        if (currentStatus == newStatus) {
            return ResponseEntity.ok(mapTaskDetailResponse(task));
        }

        Set<TaskStatus> validNextStates = VALID_TRANSITIONS.get(currentStatus);
        if (validNextStates == null || !validNextStates.contains(newStatus)) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                            "error", "Invalid status transition from " + currentStatus + " to " + newStatus,        
                            "validTransitions", validNextStates
                        )
                    );
        }

        task.setStatus(newStatus);
        if (newStatus == TaskStatus.BLOCKED) {
            task.setBlockedAt(OffsetDateTime.now());
        } else if (currentStatus == TaskStatus.BLOCKED) {
            task.setBlockedAt(null);
        }
        Tasks savedTask = tasksRepository.save(task);
        return ResponseEntity.ok(mapTaskDetailResponse(savedTask));
    }

    public ResponseEntity<?> deleteTask(UUID taskId) {
        if (!tasksRepository.existsById(taskId)) {
            return ResponseEntity.notFound().build();
        }
        taskAssignmentsRepository.deleteByTaskId(taskId);
        tasksRepository.deleteById(taskId);
        return ResponseEntity.ok(Map.of("deleted", true));
    }

    public ResponseEntity<?> updateTaskAssignees(UUID taskId, TaskAssigneeUpdateRequest request) {
        if (!tasksRepository.existsById(taskId)) {
            return ResponseEntity.notFound().build();
        }

        List<UUID> assigneeIds = request.getAssigneeIds() != null ? request.getAssigneeIds() : List.of();
        syncAssignments(taskId, assigneeIds);

        Tasks task = tasksRepository.findById(taskId).orElseThrow();
        return ResponseEntity.ok(mapTaskDetailResponse(task));
    }

    public ResponseEntity<?> getTaskStats(UUID projectId) {
        long totalProjectTasks = tasksRepository.countByProjectId(projectId);

        Optional<Sprints> activeSprint = sprintsRepository.findByProjectIdAndStatus(projectId, SprintStatus.ACTIVE);
        long totalCurrentSprintTasks = activeSprint
                .map(sprint -> tasksRepository.countBySprintId(sprint.getSprintId()))
                .orElse(0L);

        long totalCompletedTasks = tasksRepository.countByProjectIdAndStatus(projectId, TaskStatus.DONE);
        long totalReviewTasks = tasksRepository.countByProjectIdAndStatus(projectId, TaskStatus.REVIEW);
        long totalBlockedTasks = tasksRepository.countByProjectIdAndStatus(projectId, TaskStatus.BLOCKED);

        return ResponseEntity.ok(new TaskStatsResponse(
                totalProjectTasks,
                totalCurrentSprintTasks,
                totalCompletedTasks,
                totalReviewTasks,
                totalBlockedTasks
        ));
    }

    private ResponseEntity<?> validateCreateOrUpdateRequest(
            UUID projectId,
            String title,
            Integer storyPoints,
            UUID sprintId
    ) {
        if (projectId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Project ID is required"));
        }

        if (!projectsRepository.existsById(projectId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Project not found"));
        }

        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Title is required"));
        }

        if (title.length() > 255) {
            return ResponseEntity.badRequest().body(Map.of("error", "Title must be at most 255 characters"));
        }

        if (storyPoints != null && (storyPoints < 0 || storyPoints > 4)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Estimated hours must be between 0 and 4. Consider splitting the task into subtasks."
            ));
        }

        if (sprintId != null) {
            Optional<Sprints> sprint = sprintsRepository.findById(sprintId);
            if (sprint.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Sprint not found"));
            }
            if (!sprint.get().getProjectId().equals(projectId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Sprint does not belong to the specified project"));
            }
        }

        return null;
    }

    private void syncAssignments(UUID taskId, List<UUID> assigneeIds) {
        List<TaskAssignments> currentAssignments = taskAssignmentsRepository.findByTaskId(taskId);
        Set<UUID> currentUserIds = currentAssignments.stream()
                .map(TaskAssignments::getUserId)
                .collect(Collectors.toSet());

        Set<UUID> requestedUserIds = new HashSet<>(assigneeIds);

        long foundCount = usersRepository.countByUserIdIn(requestedUserIds);
        if (foundCount != requestedUserIds.size()) {
            throw new ResourceNotFoundException("One or more users not found");
        }

        for (UUID userId : requestedUserIds) {
            if (!currentUserIds.contains(userId)) {
                taskAssignmentsRepository.save(new TaskAssignments(taskId, userId));
            }
        }

        for (UUID existingUserId : currentUserIds) {
            if (!requestedUserIds.contains(existingUserId)) {
                taskAssignmentsRepository.deleteById(new TaskAssignmentsId(taskId, existingUserId));
            }
        }
    }

    private List<TaskAssigneeResponse> getTaskAssignees(UUID taskId) {
        List<TaskAssignments> assignments = taskAssignmentsRepository.findByTaskId(taskId);
        List<UUID> userIds = assignments.stream()
                .map(TaskAssignments::getUserId)
                .collect(Collectors.toList());

        return usersRepository.findAllById(userIds).stream()
                .map(user -> new TaskAssigneeResponse(user.getUserId(), user.getUsername()))
                .collect(Collectors.toList());
    }

    private String getSprintName(UUID sprintId) {
        if (sprintId == null) return null;
        return sprintsRepository.findById(sprintId).map(Sprints::getName).orElse(null);
    }

    private String getProjectName(UUID projectId) {
        return projectsRepository.findById(projectId).map(Projects::getName).orElse(null);
    }

    private TaskSummaryResponse mapTaskSummaryResponse(Tasks task) {
        return new TaskSummaryResponse(
                task.getTaskId(),
                task.getProjectId(),
                task.getTitle(),
                task.getStatus(),
                task.getPriority(),
                task.getStoryPoints(),
                task.getSprintId(),
                getSprintName(task.getSprintId()),
                getTaskAssignees(task.getTaskId()),
                task.getStatus() == TaskStatus.BLOCKED,
                task.getStatus() == TaskStatus.REVIEW,
                task.getCreatedAt(),
                task.getStartedAt(),
                task.getDeliveredAt(),
                task.getActualHours()
        );
    }

    private TaskDetailResponse mapTaskDetailResponse(Tasks task) {
        return new TaskDetailResponse(
                task.getTaskId(),
                task.getProjectId(),
                getProjectName(task.getProjectId()),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getStoryPoints(),
                task.getSprintId(),
                getSprintName(task.getSprintId()),
                getTaskAssignees(task.getTaskId()),
                task.getStatus() == TaskStatus.BLOCKED,
                task.getStatus() == TaskStatus.REVIEW,
                task.getCreatedAt(),
                task.getStartedAt(),
                task.getDeliveredAt(),
                task.getBlockedAt(),
                task.getActualHours(),
                task.getAiFlagged()
        );
    }

}
