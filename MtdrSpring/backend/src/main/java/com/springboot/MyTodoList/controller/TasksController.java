package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.CreateTaskRequest;
import com.springboot.MyTodoList.dto.StatusUpdateRequest;
import com.springboot.MyTodoList.dto.TaskAssigneeUpdateRequest;
import com.springboot.MyTodoList.dto.TaskSummaryResponse;
import com.springboot.MyTodoList.dto.UpdateTaskRequest;
import com.springboot.MyTodoList.service.TasksService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/api/tasks")
public class TasksController {

    @Autowired
    private TasksService tasksService;

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody CreateTaskRequest request) {
        return tasksService.createTask(request);
    }

    @GetMapping
    public ResponseEntity<List<TaskSummaryResponse>> getAllTasks() {
        return ResponseEntity.ok(tasksService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable UUID id) {
        return tasksService.getTaskById(id);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskSummaryResponse>> getTasksByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(tasksService.getTasksByProject(projectId));
    }

    @GetMapping("/project/{projectId}/backlog")
    public ResponseEntity<List<TaskSummaryResponse>> getBacklogByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(tasksService.getBacklogByProject(projectId));
    }
    
    @GetMapping("/project/{projectId}/stats")
    public ResponseEntity<?> getTaskStats(@PathVariable UUID projectId) {
        return tasksService.getTaskStats(projectId);
    }

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<List<TaskSummaryResponse>> getTasksBySprint(@PathVariable UUID sprintId) {
        return ResponseEntity.ok(tasksService.getTasksBySprint(sprintId));
    }

    @GetMapping("/sprint/{sprintId}/by-status")
    public ResponseEntity<?> getSprintTasksByStatus(@PathVariable UUID sprintId) {
        return tasksService.getSprintTasksByStatus(sprintId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable UUID id, @RequestBody UpdateTaskRequest request) {
        return tasksService.updateTask(id, request);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable UUID id, @RequestBody StatusUpdateRequest request) {
        return tasksService.updateStatus(id, request.getStatus());
    }

    @PutMapping("/{id}/assignees")
    public ResponseEntity<?> updateTaskAssignees(@PathVariable UUID id, @RequestBody TaskAssigneeUpdateRequest request) {
        return tasksService.updateTaskAssignees(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable UUID id) {
        return tasksService.deleteTask(id);
    }
}
