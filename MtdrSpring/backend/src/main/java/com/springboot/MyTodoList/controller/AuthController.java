package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.CreateUserRequest;
import com.springboot.MyTodoList.dto.LoginRequest;
import com.springboot.MyTodoList.dto.LoginResponse;
import com.springboot.MyTodoList.model.UserRole;
import com.springboot.MyTodoList.model.Users;
import com.springboot.MyTodoList.repository.UsersRepository;
import com.springboot.MyTodoList.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Users user = usersRepository.findByUsername(loginRequest.getUsername()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getUsername());

        LoginResponse response = new LoginResponse(
            token,
            user.getUsername(),
            user.getEmail(),
            user.getUserRole().name()
        );
        System.out.println("Sesion Iniciada con exito por: "+user.getUsername());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody CreateUserRequest request) {
        if (usersRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (usersRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Users newUser = new Users();
        newUser.setUserId(UUID.randomUUID());
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setCellPhone(request.getCellPhone());
        newUser.setUserRole(request.getUserRole() != null ? request.getUserRole() : UserRole.DEVELOPER);
        newUser.setCreatedAt(OffsetDateTime.now());

        usersRepository.save(newUser);

        String token = jwtTokenProvider.generateToken(newUser.getUserId(), newUser.getUsername());

        LoginResponse response = new LoginResponse(
            token,
            newUser.getUsername(),
            newUser.getEmail(),
            newUser.getUserRole().name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}