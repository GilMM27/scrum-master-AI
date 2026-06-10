package com.springboot.MyTodoList.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.MyTodoList.model.Tasks;
import com.springboot.MyTodoList.model.TaskStatus;
import java.time.OffsetDateTime;


public interface TasksRepository extends JpaRepository<Tasks, UUID> {

    List<Tasks> findByProjectId(UUID projectId);

    List<Tasks> findByProjectIdOrderByCreatedAtDesc(UUID projectId);

    List<Tasks> findByProjectIdAndSprintIdIsNullOrderByCreatedAtDesc(UUID projectId);

    List<Tasks> findBySprintId(UUID sprintId);

    List<Tasks> findBySprintIdOrderByCreatedAtDesc(UUID sprintId);

    List<Tasks> findBySprintIdAndStatus(UUID sprintId, TaskStatus status);

    List<Tasks> findByStatus(TaskStatus status);

    List<Tasks> findByIsAiFlaggedTrue();

    List<Tasks> findByBlockedAtBefore(OffsetDateTime blockedAt);

    List<Tasks> findByDeliveredAtIsNull();

    Long countBySprintIdAndStatus(UUID sprintId, TaskStatus status);

    Long countBySprintId(UUID sprintId);

    Long countByProjectId(UUID projectId);

    Long countByProjectIdAndStatus(UUID projectId, TaskStatus status);

    Optional<Tasks> findByTaskId(UUID taskId);

    boolean existsByTaskId(UUID taskId);

    List<Tasks> findBySprintIdOrderByStoryPoints(UUID sprintId);

    @Query("SELECT t FROM Tasks t WHERE t.projectId = :projectId AND (t.createdAt >= :since OR t.startedAt >= :since OR t.deliveredAt >= :since)")
    List<Tasks> findRecentActivityByProjectId(@Param("projectId") UUID projectId, @Param("since") OffsetDateTime since);
    
    void deleteByProjectId(UUID projectId);

    void deleteBySprintId(UUID sprintId);
    
}
