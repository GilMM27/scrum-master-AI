package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.UpdateSprintStatusRequest;
import com.springboot.MyTodoList.dto.CreateSprintRequest;
import com.springboot.MyTodoList.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/{sprintId}/status")
    public ResponseEntity<?> updateSprintStatus(@PathVariable UUID sprintId,
                                                @RequestBody UpdateSprintStatusRequest request) {
        return sprintService.updateSprintStatus(sprintId, request);
    }
}
