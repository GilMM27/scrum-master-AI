package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.ProjectDeveloperResponse;
import com.springboot.MyTodoList.exception.ResourceNotFoundException;
import com.springboot.MyTodoList.model.AccountStatus;
import com.springboot.MyTodoList.model.ProjectMembers;
import com.springboot.MyTodoList.model.Projects;
import com.springboot.MyTodoList.model.UserRole;
import com.springboot.MyTodoList.model.Users;
import com.springboot.MyTodoList.repository.ProjectMembersRepository;
import com.springboot.MyTodoList.repository.ProjectsRepository;
import com.springboot.MyTodoList.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectMemberService {

    @Autowired
    private ProjectMembersRepository projectMembersRepository;

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private UsersRepository usersRepository;

    public ResponseEntity<?> addMemberToProject(UUID projectId, UUID userId) {
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (projectMembersRepository.existsByProjectIdAndUserId(projectId, userId)) {
            return ResponseEntity.badRequest().body("User is already a member of this project");
        }

        ProjectMembers member = new ProjectMembers(projectId, userId, OffsetDateTime.now());
        projectMembersRepository.save(member);

        return ResponseEntity.ok().body(member);
    }
    
    @Transactional
    public ResponseEntity<?> removeMemberFromProject(UUID projectId, UUID userId) {
        if (!projectMembersRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new ResourceNotFoundException("User is not a member of this project");
        }

        projectMembersRepository.deleteByProjectIdAndUserId(projectId, userId);
        return ResponseEntity.ok("User removed from project successfully");
    }

    public List<ProjectMembers> getProjectMembers(UUID projectId) {
        return projectMembersRepository.findByProjectId(projectId);
    }

    public List<ProjectMembers> getUserProjects(UUID userId) {
        return projectMembersRepository.findByUserId(userId);
    }

    public List<ProjectDeveloperResponse> getActiveDevelopersByProject(UUID projectId) {
        List<ProjectMembers> members = projectMembersRepository.findByProjectId(projectId);

        List<UUID> userIds = members.stream()
                .map(ProjectMembers::getUserId)
                .collect(Collectors.toList());
        
        return usersRepository.findAllById(userIds).stream()
                .filter(user -> user.getUserRole() == UserRole.DEVELOPER)
                .filter(user -> user.getAccountStatus() == AccountStatus.ACTIVE)
                .map(user -> new ProjectDeveloperResponse(user.getUserId(), user.getUsername()))
                .collect(Collectors.toList());
    }
}