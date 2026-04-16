package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.CreateTaskRequest;
import com.springboot.MyTodoList.dto.StatusUpdateRequest;
import com.springboot.MyTodoList.model.TaskStatus;
import com.springboot.MyTodoList.model.Tasks;
import com.springboot.MyTodoList.service.TasksService;

import okhttp3.internal.concurrent.Task;

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
    public ResponseEntity<List<Tasks>> getAllTasks() {
        return ResponseEntity.ok(tasksService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable UUID id) {
        return tasksService.getTaskById(id);
    }

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<List<Tasks>> getTasksBySprint(@PathVariable UUID sprintId) {
        return ResponseEntity.ok(tasksService.getTasksBySprint(sprintId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Tasks>> getTasksByStatus(@PathVariable TaskStatus status) {
        return ResponseEntity.ok(tasksService.getTasksByStatus(status));
    }

    @GetMapping("/blocked")
    public ResponseEntity<List<Tasks>> getBlockedTasks() {
        return ResponseEntity.ok(tasksService.getBlockedTasks());
    }

    @GetMapping("/undelivered")
    public ResponseEntity<List<Tasks>> getUndeliveredTasks() {
        return ResponseEntity.ok(tasksService.getUndeliveredTasks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable UUID id, @RequestBody Tasks task) {
        return tasksService.updateTask(id, task);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable UUID id, @RequestBody StatusUpdateRequest request) {
        return tasksService.updateStatus(id, request.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable UUID id) {
        return tasksService.deleteTask(id);
    }

    @PostMapping("/{taskId}/assignees/{userId}")
    public ResponseEntity<?> assignUserToTask(@PathVariable UUID taskId, @PathVariable UUID userId) {
        return tasksService.assignUserToTask(taskId, userId);
    }

    @DeleteMapping("/{taskId}/assignees/{userId}")
    public ResponseEntity<?> unassignUserFromTask(@PathVariable UUID taskId, @PathVariable UUID userId) {
        return tasksService.unassignUserFromTask(taskId, userId);
    }
}
