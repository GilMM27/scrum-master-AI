package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.UpdateSprintStatusRequest;
import com.springboot.MyTodoList.dto.UpdateSprintRequest;
import com.springboot.MyTodoList.dto.CreateSprintRequest;
import com.springboot.MyTodoList.dto.SprintOptionResponse;
import com.springboot.MyTodoList.dto.SprintSummaryResponse;
import com.springboot.MyTodoList.model.SprintStatus;
import com.springboot.MyTodoList.model.Sprints;
import com.springboot.MyTodoList.model.TaskStatus;
import com.springboot.MyTodoList.model.Tasks;
import com.springboot.MyTodoList.repository.ProjectsRepository;
import com.springboot.MyTodoList.repository.SprintsRepository;
import com.springboot.MyTodoList.repository.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SprintService {

    @Autowired
    private SprintsRepository sprintsRepository;

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private TasksRepository tasksRepository;

    public ResponseEntity<?> createSprint(CreateSprintRequest request) {
        if (request.getProjectId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Project ID is required"));
        }

        if (!projectsRepository.existsById(request.getProjectId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Project not found"));
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name is required"));
        }

        if (request.getName().length() > 255) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name must be at most 255 characters"));
        }

        if (!request.getName().matches(".*\\d.*")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Sprint name must include a numeric indicator (e.g. 'Sprint 1')"));
        }

        if (request.getStartDate() != null && request.getEndDate() != null) {
            long daysBetween = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
            if (daysBetween < 7) {
                return ResponseEntity.badRequest().body(Map.of("error", "Sprint duration must be at least 1 week"));
            }
            if (daysBetween > 28) {
                return ResponseEntity.badRequest().body(Map.of("error", "Sprint duration must not exceed 4 weeks"));
            }
        }

        Sprints sprint = request.toEntity();
        Sprints savedSprint = sprintsRepository.save(sprint);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSprint);
    }

    public ResponseEntity<?> updateSprintStatus(UUID sprintId, UpdateSprintStatusRequest request) {
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
        }

        SprintStatus newStatus;
        try {
            newStatus = SprintStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid status. Must be PLANNED, ACTIVE, or CLOSED"));
        }

        Optional<Sprints> sprintOpt = sprintsRepository.findById(sprintId);
        if (sprintOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Sprints sprint = sprintOpt.get();
        SprintStatus currentStatus = sprint.getStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid status transition from " + currentStatus + " to " + newStatus));
        }

        if (newStatus == SprintStatus.ACTIVE && (request.getStartDate() == null && sprint.getStartDate() == null )) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Start date is required when activating a sprint"));
        }

        if (newStatus == SprintStatus.CLOSED && (request.getEndDate() == null && sprint.getEndDate() == null )) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "End date is required when closing a sprint"));
        }

        sprint.setStatus(newStatus);
        if (request.getStartDate() != null) {
            sprint.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            sprint.setEndDate(request.getEndDate());
        }

        Sprints updatedSprint = sprintsRepository.save(sprint);
        return ResponseEntity.ok(updatedSprint);
    }

    public List<SprintOptionResponse> getProjectSprints(UUID projectId) {
        return sprintsRepository.findByProjectIdOrderByStartDateDesc(projectId).stream()
                .map(this::mapSprintOption)
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> getActiveSprint(UUID projectId) {
        return sprintsRepository.findByProjectIdAndStatus(projectId, SprintStatus.ACTIVE)
                .<ResponseEntity<?>>map(sprint -> ResponseEntity.ok(mapSprintOption(sprint)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public List<SprintOptionResponse> getAvailableSprints(UUID projectId) {
        return sprintsRepository.findByProjectIdOrderByStartDateDesc(projectId).stream()
                .filter(sprint -> sprint.getStatus() == SprintStatus.ACTIVE || sprint.getStatus() == SprintStatus.PLANNED)
                .map(this::mapSprintOption)
                .collect(Collectors.toList());
    }

    private SprintOptionResponse mapSprintOption(Sprints sprint) {
        return new SprintOptionResponse(
                sprint.getSprintId(),
                sprint.getName(),
                sprint.getStartDate(),
                sprint.getEndDate(),
                sprint.getStatus()
        );
    }

    public ResponseEntity<?> updateSprint(UUID sprintId, UpdateSprintRequest request) {
        Optional<Sprints> sprintOpt = sprintsRepository.findById(sprintId);
        if (sprintOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Sprints sprint = sprintOpt.get();

        if (sprint.getStatus() == SprintStatus.CLOSED) {
            return ResponseEntity.badRequest().body(Map.of("error", "A closed sprint cannot be modified"));
        }

        // Validate and apply name change
        if (request.getName() != null) {
            String name = request.getName().trim();
            if (name.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Name cannot be empty"));
            }
            if (name.length() > 255) {
                return ResponseEntity.badRequest().body(Map.of("error", "Name must be at most 255 characters"));
            }
            if (!name.matches(".*\\d.*")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Sprint name must include a numeric indicator (e.g. 'Sprint 1')"));
            }
            sprint.setName(name);
        }

        // Validate and apply date changes
        LocalDate effectiveStart = request.getStartDate() != null ? request.getStartDate() : sprint.getStartDate();
        LocalDate effectiveEnd = request.getEndDate() != null ? request.getEndDate() : sprint.getEndDate();

        if (effectiveStart != null && effectiveEnd != null) {
            long daysBetween = ChronoUnit.DAYS.between(effectiveStart, effectiveEnd);
            if (daysBetween < 7) {
                return ResponseEntity.badRequest().body(Map.of("error", "Sprint duration must be at least 1 week"));
            }
            if (daysBetween > 28) {
                return ResponseEntity.badRequest().body(Map.of("error", "Sprint duration must not exceed 4 weeks"));
            }
        }

        if (request.getStartDate() != null) sprint.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) sprint.setEndDate(request.getEndDate());

        // Validate and apply status change
        if (request.getStatus() != null) {
            SprintStatus newStatus;
            try {
                newStatus = SprintStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid status. Must be PLANNED, ACTIVE, or CLOSED"));
            }

            SprintStatus currentStatus = sprint.getStatus();
            if (newStatus != currentStatus) {
                if (!isValidTransition(currentStatus, newStatus)) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid status transition from " + currentStatus + " to " + newStatus));
                }

                if (newStatus == SprintStatus.ACTIVE && sprint.getStartDate() == null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Start date is required when activating a sprint"));
                }

                if (newStatus == SprintStatus.CLOSED) {
                    LocalDate endDate = sprint.getEndDate();
                    if (endDate == null) {
                        return ResponseEntity.badRequest().body(Map.of("error", "End date is required when closing a sprint"));
                    }
                    if (LocalDate.now().isBefore(endDate.plusDays(2))) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "Sprint can only be closed when its end date was at least 2 days ago"));
                    }
                    if (currentStatus != SprintStatus.ACTIVE) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "Only active sprints can be closed"));
                    }
                }

                sprint.setStatus(newStatus);
            }
        }

        Sprints updated = sprintsRepository.save(sprint);
        return ResponseEntity.ok(mapSprintOption(updated));
    }

    public ResponseEntity<?> getSprintSummary(UUID sprintId) {
        Optional<Sprints> sprintOpt = sprintsRepository.findById(sprintId);
        if (sprintOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Sprints sprint = sprintOpt.get();
        List<Tasks> tasks = tasksRepository.findBySprintId(sprintId);

        long totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        long inProgressTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long reviewTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.REVIEW).count();
        long blockedTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.BLOCKED).count();
        long toDoTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.TO_DO).count();

        double totalEstimatedHours = tasks.stream().mapToInt(Tasks::getStoryPoints).sum();

        boolean hasActualHours = tasks.stream().anyMatch(t -> t.getActualHours() != null);
        Double totalActualHours = hasActualHours
                ? tasks.stream().filter(t -> t.getActualHours() != null).mapToDouble(Tasks::getActualHours).sum()
                : null;

        SprintSummaryResponse summary = new SprintSummaryResponse(
                sprint.getName(),
                totalTasks,
                completedTasks,
                inProgressTasks,
                reviewTasks,
                blockedTasks,
                toDoTasks,
                totalEstimatedHours,
                totalActualHours
        );

        return ResponseEntity.ok(summary);
    }

    private boolean isValidTransition(SprintStatus from, SprintStatus to) {
        switch (from) {
            case PLANNED:
                return to == SprintStatus.ACTIVE;
            case ACTIVE:
                return to == SprintStatus.CLOSED;
            case CLOSED:
            default:
                return false;
        }
    }
}
