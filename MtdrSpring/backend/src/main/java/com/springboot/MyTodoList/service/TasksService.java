package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.CreateTaskRequest;
import com.springboot.MyTodoList.model.Sprints;
import com.springboot.MyTodoList.model.TaskStatus;
import com.springboot.MyTodoList.model.Tasks;
import com.springboot.MyTodoList.model.TaskAssignments;
import com.springboot.MyTodoList.model.TaskAssignmentsId;
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

@Service
@Transactional
public class TasksService {

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private SprintsRepository sprintsRepository;

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
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Title is required"));
        }

        if (request.getTitle().length() > 255) {
            return ResponseEntity.badRequest().body(Map.of("error", "Title must be at most 255 characters"));
        }

        if(request.getSprintId() != null){
            Optional<Sprints> sprintOpt = sprintsRepository.findById(request.getSprintId());
            if (sprintOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Sprint not found"));
            }
        }
        

        if (request.getAssigneeId() != null) {
            if (!usersRepository.existsById(request.getAssigneeId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Assignee user not found"));
            }
        }

        Tasks task = request.toEntity();
        Tasks savedTask = tasksRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    public List<Tasks> getAllTasks() {
        return tasksRepository.findAll();
    }

    public ResponseEntity<?> getTaskById(UUID taskId) {
        Optional<Tasks> taskOpt = tasksRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(taskOpt.get());
    }

    public List<Tasks> getTasksBySprint(UUID sprintId) {
        return tasksRepository.findBySprintIdOrderByCreatedAtDesc(sprintId);
    }

    public List<Tasks> getTasksByStatus(TaskStatus status) {
        return tasksRepository.findByStatus(status);
    }

    public List<Tasks> getBlockedTasks() {
        return tasksRepository.findByBlockedAtBefore(OffsetDateTime.now());
    }

    public List<Tasks> getUndeliveredTasks() {
        return tasksRepository.findByDeliveredAtIsNull();
    }

    public ResponseEntity<?> updateTask(UUID taskId, Tasks updates) {
        Optional<Tasks> taskOpt = tasksRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tasks task = taskOpt.get();

        if (updates.getTitle() != null) {
            if (updates.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Title cannot be empty"));
            }
            if (updates.getTitle().length() > 255) {
                return ResponseEntity.badRequest().body(Map.of("error", "Title must be at most 255 characters"));
            }
            task.setTitle(updates.getTitle().trim());
        }

        if (updates.getDescription() != null) {
            task.setDescription(updates.getDescription());
        }
        
        if(updates.getStoryPoints() < 0){
            return ResponseEntity.badRequest().body(Map.of("error", "Story points cant be negative"));
        }else{
            task.setStoryPoints(updates.getStoryPoints());
        }

        if(updates.getSprintId() != null){
            Optional<Sprints> sprint = sprintsRepository.findById(updates.getSprintId());
            if(sprint.isEmpty()){
                return ResponseEntity.badRequest().body(Map.of("error", "Sprint not found"));
            }else{
                task.setSprintId(updates.getSprintId());
            }
        }

        if(updates.getAiFlagged() != null){
            task.setAiFlagged(updates.getAiFlagged());
        }

        Tasks savedTask = tasksRepository.save(task);
        return ResponseEntity.ok().body(savedTask);

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
            return ResponseEntity.ok(task);
        }

        Set<TaskStatus> validNextStates = VALID_TRANSITIONS.get(currentStatus);
        if (validNextStates == null || !validNextStates.contains(newStatus)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid status transition from " + currentStatus + " to " + newStatus,
                            "validTransitions", validNextStates));
        }

        task.setStatus(newStatus);

        if (newStatus == TaskStatus.BLOCKED) {
            task.setBlockedAt(OffsetDateTime.now());
        } else {
            task.setBlockedAt(null);
        }

        

        Tasks savedTask = tasksRepository.save(task);
        return ResponseEntity.ok(savedTask);
    }

    public ResponseEntity<?> deleteTask(UUID taskId) {
        if (!tasksRepository.existsById(taskId)) {
            return ResponseEntity.notFound().build();
        }
        tasksRepository.deleteById(taskId);
        return ResponseEntity.ok(Map.of("deleted", true));
    }

    public ResponseEntity<?> assignUserToTask(UUID taskId, UUID userId) {
        if (!tasksRepository.existsById(taskId)) {
            return ResponseEntity.notFound().build();
        }

        if (!usersRepository.existsById(userId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        if (taskAssignmentsRepository.existsByTaskIdAndUserId(taskId, userId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "User is already assigned to this task"));
        }

        TaskAssignments assignment = new TaskAssignments(taskId, userId);
        taskAssignmentsRepository.save(assignment);

        return ResponseEntity.ok(Map.of("assigned", true, "taskId", taskId, "userId", userId));
    }

    public ResponseEntity<?> unassignUserFromTask(UUID taskId, UUID userId) {
        if (!tasksRepository.existsById(taskId)) {
            return ResponseEntity.notFound().build();
        }

        if (!taskAssignmentsRepository.existsByTaskIdAndUserId(taskId, userId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "User is not assigned to this task"));
        }

        TaskAssignmentsId assignmentId = new TaskAssignmentsId(taskId, userId);
        taskAssignmentsRepository.deleteById(assignmentId);

        return ResponseEntity.ok(Map.of("unassigned", true, "taskId", taskId, "userId", userId));
    }

}
