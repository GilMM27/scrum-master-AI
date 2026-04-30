package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getProjectAnalytics(@PathVariable UUID projectId) {
        return analyticsService.getProjectAnalytics(projectId);
    }

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<?> getSprintAnalytics(@PathVariable UUID sprintId) {
        return analyticsService.getSprintAnalytics(sprintId);
    }
}
