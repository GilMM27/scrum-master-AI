package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.CreateProjectRequest;
import com.springboot.MyTodoList.dto.ProjectSummaryResponse;
import com.springboot.MyTodoList.model.ProjectMembers;
import com.springboot.MyTodoList.model.Projects;
import com.springboot.MyTodoList.repository.ProjectMembersRepository;
import com.springboot.MyTodoList.repository.ProjectsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private ProjectMembersRepository projectMembersRepository;

    public ResponseEntity<?> createProject(CreateProjectRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name is required"));
        }

        if (request.getName().length() > 255) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name must be at most 255 characters"));
        }

        if (projectsRepository.existsByName(request.getName().trim())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Project with this name already exists"));
        }

        Projects project = request.toEntity();
        Projects savedProject = projectsRepository.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProject);
    }

    public ResponseEntity<?> getProjectById(UUID projectId) {
        return projectsRepository.findById(projectId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public List<ProjectSummaryResponse> getProjectsForUserSelector(UUID userId) {
        List<ProjectMembers> memberships = projectMembersRepository.findByUserId(userId);

        List<UUID> projectIds = memberships.stream()
                .map(ProjectMembers::getProjectId)
                .distinct()
                .toList();

        return projectsRepository.findAllById(projectIds).stream()
                .map(project -> new ProjectSummaryResponse(project.getProjectId(), project.getName()))
                .sorted(Comparator.comparing(ProjectSummaryResponse::getName))
                .collect(Collectors.toList());
    }
}
