package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.CreateSprintRequest;
import com.springboot.MyTodoList.model.Sprints;
import com.springboot.MyTodoList.repository.ProjectsRepository;
import com.springboot.MyTodoList.repository.SprintsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
