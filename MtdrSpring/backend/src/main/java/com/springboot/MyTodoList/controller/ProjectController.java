package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.CreateProjectRequest;
import com.springboot.MyTodoList.dto.ProjectDeveloperResponse;
import com.springboot.MyTodoList.dto.ProjectSummaryResponse;
import com.springboot.MyTodoList.model.ProjectMembers;
import com.springboot.MyTodoList.model.UserRole;
import com.springboot.MyTodoList.model.Users;
import com.springboot.MyTodoList.service.ProjectMemberService;
import com.springboot.MyTodoList.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<?> getAllProjects() {
        Users currentUser = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getUserRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }
        List<ProjectSummaryResponse> projects = projectService.getAllProjects().stream()
                .map(p -> new ProjectSummaryResponse(p.getProjectId(), p.getName()))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(projects);
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody CreateProjectRequest request) {
        Users currentUser = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return projectService.createProject(request, currentUser.getUserId());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable UUID projectId) {
        return projectService.getProjectById(projectId);
    }

    @GetMapping("/my/selector")
    public ResponseEntity<List<ProjectSummaryResponse>> getProjectsForUserSelector() {
        Users currUser = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(projectService.getProjectsForUserSelector(currUser.getUserId()));
    }

    @PostMapping("/{projectId}/members/{userId}")
    public ResponseEntity<?> addMember(@PathVariable UUID projectId, @PathVariable UUID userId) {
        Users currentUser = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getUserRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }
        return projectMemberService.addMemberToProject(projectId, userId);
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable UUID projectId, @PathVariable UUID userId) {
        Users currentUser = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getUserRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }
        return projectMemberService.removeMemberFromProject(projectId, userId);
    }

    @GetMapping("/{projectId}/members")
    public List<ProjectMembers> getProjectMembers(@PathVariable UUID projectId) {
        return projectMemberService.getProjectMembers(projectId);
    }

    @GetMapping("/{projectId}/developers")
    public ResponseEntity<List<ProjectDeveloperResponse>> getProjectDevelopers(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectMemberService.getActiveDevelopersByProject(projectId));
    }

    @GetMapping("/user/{userId}")
    public List<ProjectMembers> getUserProjects(@PathVariable UUID userId) {
        return projectMemberService.getUserProjects(userId);
    }
}