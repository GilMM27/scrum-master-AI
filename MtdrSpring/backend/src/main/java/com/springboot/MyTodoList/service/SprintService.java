package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.UpdateSprintStatusRequest;
import com.springboot.MyTodoList.dto.CreateSprintRequest;
import com.springboot.MyTodoList.model.SprintStatus;
import com.springboot.MyTodoList.model.Sprints;
import com.springboot.MyTodoList.repository.ProjectsRepository;
import com.springboot.MyTodoList.repository.SprintsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class SprintService {

    @Autowired
    private SprintsRepository sprintsRepository;

    @Autowired
    private ProjectsRepository projectsRepository;

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
