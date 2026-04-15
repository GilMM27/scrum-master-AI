package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.CreateSprintRequest;
import com.springboot.MyTodoList.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sprints")
public class SprintController {

    @Autowired
    private SprintService sprintService;

    @PostMapping
    public ResponseEntity<?> createSprint(@RequestBody CreateSprintRequest request) {
        return sprintService.createSprint(request);
    }
}
