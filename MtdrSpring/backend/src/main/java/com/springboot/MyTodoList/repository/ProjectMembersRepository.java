package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.ProjectMembers;
import com.springboot.MyTodoList.model.ProjectMembersId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectMembersRepository extends JpaRepository<ProjectMembers, ProjectMembersId> {
    List<ProjectMembers> findByProjectId(UUID projectId);
    List<ProjectMembers> findByUserId(UUID userId);
    boolean existsByProjectIdAndUserId(UUID projectId, UUID userId);
    void deleteByProjectIdAndUserId(UUID projectId, UUID userId);
}