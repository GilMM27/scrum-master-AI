package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.UpdateSprintStatusRequest;
import com.springboot.MyTodoList.dto.UpdateSprintRequest;
import com.springboot.MyTodoList.dto.CreateSprintRequest;
import com.springboot.MyTodoList.dto.SprintOptionResponse;
import com.springboot.MyTodoList.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/sprints")
public class SprintController {

    @Autowired
    private SprintService sprintService;

    @PostMapping
    public ResponseEntity<?> createSprint(@RequestBody CreateSprintRequest request) {
        return sprintService.createSprint(request);
    }

    @PutMapping("/{sprintId}")
    public ResponseEntity<?> updateSprint(@PathVariable UUID sprintId, @RequestBody UpdateSprintRequest request) {
        return sprintService.updateSprint(sprintId, request);
    }

    @PatchMapping("/{sprintId}/status")
    public ResponseEntity<?> updateSprintStatus(@PathVariable UUID sprintId, @RequestBody UpdateSprintStatusRequest request) {
        return sprintService.updateSprintStatus(sprintId, request);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<SprintOptionResponse>> getProjectSprints(@PathVariable UUID projectId) {
        return ResponseEntity.ok(sprintService.getProjectSprints(projectId));
    }

    @GetMapping("/project/{projectId}/active")
    public ResponseEntity<?> getActiveSprint(@PathVariable UUID projectId) {
        return sprintService.getActiveSprint(projectId);
    }

    @GetMapping("/project/{projectId}/available")
    public ResponseEntity<List<SprintOptionResponse>> getAvailableSprints(@PathVariable UUID projectId) {
        return ResponseEntity.ok(sprintService.getAvailableSprints(projectId));
    }

    @GetMapping("/{sprintId}/summary")
    public ResponseEntity<?> getSprintSummary(@PathVariable UUID sprintId) {
        return sprintService.getSprintSummary(sprintId);
    }
}
