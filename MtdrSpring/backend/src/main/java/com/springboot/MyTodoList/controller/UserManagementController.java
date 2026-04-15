package com.springboot.MyTodoList.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.dto.CreateUserRequest;
import com.springboot.MyTodoList.dto.UpdateUserAuthorizationRequest;
import com.springboot.MyTodoList.dto.UpdateUserRoleRequest;
import com.springboot.MyTodoList.dto.UserDetailResponse;
import com.springboot.MyTodoList.dto.UserSummaryResponse;
import com.springboot.MyTodoList.service.UserManagementService;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {
    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping
    public ResponseEntity<List<UserSummaryResponse>> getAllUsers() {
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserDetailResponse> createManagedUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userManagementService.createManagedUser(request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(userManagementService.getUserById(userId));
    }
    
    @PatchMapping("/{userId}/role")
    public ResponseEntity<UserDetailResponse> updateUserRole(
        @PathVariable UUID userId, 
        @RequestBody UpdateUserRoleRequest request
    ) {
        return ResponseEntity.ok(userManagementService.updateUserRole(userId, request));
    }

    @PatchMapping("/{userId}/authorization")
    public ResponseEntity<UserDetailResponse> updateUserAuthorization(
        @PathVariable UUID userId, 
        @RequestBody UpdateUserAuthorizationRequest request
    ) {
        return ResponseEntity.ok(userManagementService.updateUserAuthorization(userId, request));
    }
}
