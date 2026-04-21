package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.CreateProjectRequest;
import com.springboot.MyTodoList.dto.ProjectDeveloperResponse;
import com.springboot.MyTodoList.dto.ProjectSummaryResponse;
import com.springboot.MyTodoList.model.ProjectMembers;
import com.springboot.MyTodoList.service.ProjectMemberService;
import com.springboot.MyTodoList.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody CreateProjectRequest request) {
        return projectService.createProject(request);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable UUID projectId) {
        return projectService.getProjectById(projectId);
    }

    @GetMapping("/user/{userId}/selector")
    public ResponseEntity<List<ProjectSummaryResponse>> getProjectsForUserSelector(@PathVariable UUID userId) {
        return ResponseEntity.ok(projectService.getProjectsForUserSelector(userId));
    }
    
    @PostMapping("/{projectId}/members/{userId}")
    public ResponseEntity<?> addMember(@PathVariable UUID projectId, @PathVariable UUID userId) {
        return projectMemberService.addMemberToProject(projectId, userId);
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable UUID projectId, @PathVariable UUID userId) {
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