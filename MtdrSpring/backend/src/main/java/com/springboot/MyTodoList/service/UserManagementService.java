package com.springboot.MyTodoList.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.MyTodoList.dto.CreateUserRequest;
import com.springboot.MyTodoList.dto.UpdateUserAuthorizationRequest;
import com.springboot.MyTodoList.dto.UpdateUserRoleRequest;
import com.springboot.MyTodoList.dto.UserDetailResponse;
import com.springboot.MyTodoList.dto.UserSummaryResponse;
import com.springboot.MyTodoList.exception.BadRequestException;
import com.springboot.MyTodoList.exception.ResourceNotFoundException;
import com.springboot.MyTodoList.model.AccountStatus;
import com.springboot.MyTodoList.model.UserRole;
import com.springboot.MyTodoList.model.Users;
import com.springboot.MyTodoList.repository.UsersRepository;

@Service
@Transactional
public class UserManagementService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserSummaryResponse> getAllUsers() {
        return usersRepository.findAll().stream()
            .map(this::mapToUserSummaryResponse)
            .collect(Collectors.toList());
    }

    public UserDetailResponse createManagedUser(CreateUserRequest request) {
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("A user with this email already exists.");
        }
        
        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("A user with this username already exists.");
        }

        Users newUser = new Users();
        newUser.setUserId(UUID.randomUUID());
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setCellPhone(request.getCellPhone());
        newUser.setUserRole(request.getUserRole() != null ? request.getUserRole() : UserRole.DEVELOPER);
        newUser.setAccountStatus(AccountStatus.ACTIVE);
        newUser.setCreatedAt(OffsetDateTime.now());

        Users saved = usersRepository.save(newUser);
        return mapToUserDetailResponse(saved);
    }

    public UserDetailResponse getUserById(UUID userId) {
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return mapToUserDetailResponse(user);
    }

    public UserDetailResponse updateUserRole(UUID userId, UpdateUserRoleRequest request) {
        if (request == null || request.getUserRole() == null) {
            throw new BadRequestException("User role is required.");
        }
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getUserRole() == UserRole.ADMIN && request.getUserRole() != UserRole.ADMIN) {
            throw new BadRequestException("Administrator accounts cannot have their role changed.");
        }

        user.setUserRole(request.getUserRole());
       
        Users updatedUser = usersRepository.save(user);
        return mapToUserDetailResponse(updatedUser);
    }

    public UserDetailResponse updateUserAuthorization(UUID userId, UpdateUserAuthorizationRequest request) {
        if (request == null || request.getAccountStatus() == null) {
            throw new BadRequestException("Account status is required.");
        }
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getUserRole() == UserRole.ADMIN && request.getAccountStatus() == AccountStatus.INACTIVE) {
            throw new BadRequestException("Administrator accounts cannot be deactivated.");
        }

        user.setAccountStatus(request.getAccountStatus());
       
        Users updatedUser = usersRepository.save(user);
        return mapToUserDetailResponse(updatedUser);
    }

    private UserSummaryResponse mapToUserSummaryResponse(Users user) {
        return new UserSummaryResponse(
            user.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getUserRole(),
            user.getAccountStatus()
        );
    }

    private UserDetailResponse mapToUserDetailResponse(Users user) {
        return new UserDetailResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRole(),
                user.getAccountStatus(),
                user.getTelegramId(),
                user.getCellPhone(),
                user.getCreatedAt()
        );
    }

}
